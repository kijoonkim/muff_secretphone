package com.muffinmanz.muff_secretphone.ui.video

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.util.Log
import android.util.Range
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.concurrent.futures.await
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.VIDEO_FOLDER
import com.muffinmanz.muff_secretphone.data.model.CameraCapability
import com.muffinmanz.muff_secretphone.data.model.UiState
import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import com.muffinmanz.muff_secretphone.data.prefs.AppPreferencesHelper
import com.muffinmanz.muff_secretphone.databinding.FragmentVideoBinding
import com.muffinmanz.muff_secretphone.extensions.getAspectRatio
import com.muffinmanz.muff_secretphone.extensions.getAspectRatioString
import com.muffinmanz.muff_secretphone.extensions.getNameString
import com.muffinmanz.muff_secretphone.ui.base.BaseFragment
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@AndroidEntryPoint
class VideoFragment : BaseFragment<FragmentVideoBinding, VideoViewModel>(R.layout.fragment_video) {

  private val TAG = "# VideoFragment";

  override val viewModel: VideoViewModel by viewModels()

  private val args: VideoFragmentArgs by navArgs()

  private val videoModel by lazy {
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val adapter: JsonAdapter<VideoListModel> = moshi.adapter(VideoListModel::class.java)
    adapter.fromJson(args.videoModel)!!
  }

  private val cameraCapabilities = mutableListOf<CameraCapability>()

  private lateinit var videoCapture: VideoCapture<Recorder>
  private var currentRecording: Recording? = null
  private lateinit var recordingState: VideoRecordEvent

  private var cameraIndex = 0
  private var qualityIndex = DEFAULT_QUALITY_IDX
  private var userFps = 30;

  private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }
  private var enumerationDeferred: Deferred<Unit>? = null

  override fun init() {
    initCameraFragment()
  }

  private fun initCameraFragment() {
    initializeUI()

    viewLifecycleOwner.lifecycleScope.launch {
      if (enumerationDeferred != null) {
        enumerationDeferred!!.await()
        enumerationDeferred = null
      }
      initializeQualitySectionsUI()
    }
  }

  private fun initializeUI() {
    binding.btnStartEndRecord.apply {
      setOnClickListener {
        if (!this@VideoFragment::recordingState.isInitialized || recordingState is VideoRecordEvent.Finalize) {
          enableUI(false)
          startRecording()
          text = "촬영종료"
        } else {
          when (recordingState) {
            is VideoRecordEvent.Start -> {
              currentRecording?.stop()
              currentRecording = null
            }
          }
        }
        isEnabled = false
      }
    }
  }

  @SuppressLint("MissingPermission")
  private fun startRecording() {
    val fileName = if (args.isEnglish) videoModel.examNo + ".eng.mp4" else videoModel.examNo + ".mp4"
    videoModel.videoPath = File(VIDEO_FOLDER, fileName).absolutePath;
    videoModel.isEnglish = args.isEnglish;

    val fileOutputOptions = FileOutputOptions.Builder(File(videoModel.videoPath!!)).build()
    currentRecording = videoCapture.output.prepareRecording(requireContext(), fileOutputOptions)
      .apply { withAudioEnabled() }
      .start(mainThreadExecutor, captureListener)
  }

  private val captureListener = Consumer<VideoRecordEvent> { event ->
    updateUI(event)

    if (event !is VideoRecordEvent.Status) {
      recordingState = event
    }

    if (event is VideoRecordEvent.Finalize) {
      lifecycleScope.launch {
        navigateToResult()
      }
    }
  }

  private fun updateUI(event: VideoRecordEvent) {

    when (event) {
      is VideoRecordEvent.Start -> {
        showUi(UiState.RECORDING, event.getNameString())
      }
      is VideoRecordEvent.Finalize -> {
        showUi(UiState.FINALIZED, event.getNameString())
      }
    }

    val stats = event.recordingStats
    val time = java.util.concurrent.TimeUnit.NANOSECONDS.toSeconds(stats.recordedDurationNanos)

    val recTime = (60 - time.toInt())
    binding.tvRecord.text = recTime.toString()
    if (recTime == 0) {
      currentRecording?.stop()
      currentRecording = null
    }
  }

  private fun showUi(state: UiState, status: String = "idel") {
    binding.let {
      when (state) {
        UiState.IDLE -> Unit
        UiState.RECORDING -> {
          val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.blink_animation)
          it.ivRec.startAnimation(anim)
          enableUI(true)
        }
        UiState.FINALIZED -> Unit
        else -> {
          val errorMsg = "Error: showUI($state) is not supported"
          showToast(errorMsg)
          return
        }
      }
    }
    showToast(status)
  }

  /**
   *     Quality.UHD -> "QUALITY_UHD(2160p)"
   *     Quality.FHD -> "QUALITY_FHD(1080p)"
   *     Quality.HD -> "QUALITY_HD(720p)"
   *     Quality.SD -> "QUALITY_SD(480p)"
   */
  private fun initializeQualitySectionsUI() {
    //qualityIndex = 2
    val prefs = AppPreferencesHelper(requireContext(), getString(R.string.preferences_key))
    qualityIndex = prefs.resolutionIndex!!

    viewLifecycleOwner.lifecycleScope.launch {
      bindCaptureUsecase()
    }
  }

  @SuppressLint("UnsafeOptInUsageError")
  private suspend fun bindCaptureUsecase() {
    val cameraProvider = ProcessCameraProvider.getInstance(requireContext()).await()
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    if (cameraCapabilities[cameraIndex].qualities.size <= qualityIndex) {
      qualityIndex = cameraCapabilities[cameraIndex].qualities.size - 1;
    }

    val quality = cameraCapabilities[cameraIndex].qualities[qualityIndex]
    val qualitySelector = QualitySelector.from(quality)

    binding.preview.updateLayoutParams<ConstraintLayout.LayoutParams> {
      val orientation = requireContext().resources.configuration.orientation
      dimensionRatio = quality.getAspectRatioString(quality, (orientation == Configuration.ORIENTATION_PORTRAIT))
    }

    val preview = Preview.Builder()
      .setTargetAspectRatio(quality.getAspectRatio(quality))
      .build().apply {
        setSurfaceProvider(binding.preview.surfaceProvider)
      }

    val recorder = Recorder.Builder()
      .setQualitySelector(qualitySelector)
      .build()

    //videoCapture = VideoCapture.withOutput(recorder)
    // VideoCapture.Builder를 사용하여 FPS 설정 추가
    videoCapture = VideoCapture.Builder(recorder)
      .apply {
        val prefs = AppPreferencesHelper(requireContext(), getString(R.string.preferences_key))
        userFps = prefs.fps!!
        // Camera2Interop.Extender로 FPS 설정
        val camera2Interop = Camera2Interop.Extender(this)
        camera2Interop.setCaptureRequestOption(
          CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
          Range(userFps, userFps) // FPS 범위 설정
        )
      }
      .build()

    try {
      cameraProvider.unbindAll()
      cameraProvider.bindToLifecycle(
        viewLifecycleOwner,
        cameraSelector,
        videoCapture,
        preview
      )
    } catch (exc: Exception) {
      showToast("Use case binding failed " + exc.message)
    }
  }

  init {
    enumerationDeferred = lifecycleScope.async {
      whenCreated {
        val provider = ProcessCameraProvider.getInstance(requireContext()).await()

        provider.unbindAll()
        for (camSelector in arrayOf(
          CameraSelector.DEFAULT_BACK_CAMERA,
          CameraSelector.DEFAULT_FRONT_CAMERA
        )) {
          try {
            if (provider.hasCamera(camSelector)) {
              val camera = provider.bindToLifecycle(requireActivity(), camSelector)
              QualitySelector
                .getSupportedQualities(camera.cameraInfo)
                .filter { quality ->
                  listOf(Quality.UHD, Quality.FHD, Quality.SD, Quality.HD).contains(quality)
                }.also {
                  cameraCapabilities.add(CameraCapability(camSelector, it))
                }
            }
          } catch (exc: java.lang.Exception) {
            showToast("Camera Face $camSelector is not supported")
          }
        }
      }
    }
  }

  private fun enableUI(enable: Boolean) {
    binding.btnStartEndRecord.isEnabled = enable
    // disable the camera button if no device to switch
    if (cameraCapabilities.size <= 1) {
      binding.btnStartEndRecord.isEnabled = false
    }
  }

  private fun navigateToResult() {
    Log.d(TAG, "videoModel : " + videoModel);

    if (findNavController().currentDestination?.id == R.id.videoFragment) {
      val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
      val adapter: JsonAdapter<VideoListModel> = moshi.adapter(VideoListModel::class.java)
      val jsn = adapter.toJson(videoModel)
      val directions = VideoFragmentDirections.actionVideoFragmentToVideoResultFragment(jsn)

      findNavController().navigate(directions)
    }
  }

  companion object {
    const val DEFAULT_QUALITY_IDX = 0
  }
}