package com.muffinmanz.muff_secretphone.ui.videoresult

import android.util.Log
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.EventLogger
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.data.db.VideoRecData
import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import com.muffinmanz.muff_secretphone.databinding.FragmentVideoResultBinding
import com.muffinmanz.muff_secretphone.dialog.SimpleDialog
import com.muffinmanz.muff_secretphone.extensions.observe
import com.muffinmanz.muff_secretphone.ui.base.BaseFragment
import com.muffinmanz.muff_secretphone.utilities.CommonUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class VideoResultFragment : BaseFragment<FragmentVideoResultBinding, VideoResultViewModel>(R.layout.fragment_video_result) {

  private val args: VideoResultFragmentArgs by navArgs()

  private val videoModel by lazy {
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val adapter: JsonAdapter<VideoListModel> = moshi.adapter(VideoListModel::class.java)
    adapter.fromJson(args.videoModel)!!
  }

  private var playerView: StyledPlayerView? = null
  private var player: ExoPlayer? = null

  override val viewModel: VideoResultViewModel by viewModels()

  override fun init() {
    subscribeUi()
    binding.interViewer = videoModel
    CommonUtils.bindImageFromString(binding.ivUser, videoModel.img)

    playerView = binding.playerView

    binding.playerView.apply {
      requestFocus()
    }
    binding.btnShootCancle.apply {
      setOnClickListener {
        findNavController().popBackStack()
      }
    }
    binding.btnShootEnd.apply {
      setOnClickListener {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val videoInfo = VideoRecData(
          examNo = videoModel.examNo,
          videoPath = videoModel.videoPath!!,
          saveDate = dateFormat.format(System.currentTimeMillis()),
          isEnglish = videoModel.isEnglish?.let {
            it
          }!!
        )
        SimpleDialog(context, "면접 영상 촬영 및 저장이 완료되었습니다.").show()

        viewModel.updateVideoRecInfo(videoInfo)
      }
    }
    binding.btnClose.setOnClickListener {
      if (findNavController().currentDestination?.id == R.id.videoResultFragment) {
        findNavController().popBackStack()
      }
      if (findNavController().currentDestination?.id == R.id.videoFragment) {
        findNavController().popBackStack(R.id.videoFragment, true)
      }
    }
  }

  private fun subscribeUi() {
    with(viewModel) {
      isCompleted.observe(viewLifecycleOwner, ::navigateToHome)
    }
  }

  private val TAG = this.javaClass.simpleName

  private fun navigateToHome(isComp: Boolean) {
    if (isComp && findNavController().currentDestination?.id == R.id.videoResultFragment) {
      findNavController().popBackStack(R.id.graderFragment, false)

      findNavController().getBackStackEntry(R.id.graderFragment).savedStateHandle.set("listSubscriber", "Changed")
    }
  }

  private fun initializePlayer(): Boolean {
    if (player == null) {
      if (videoModel.videoPath.isNullOrEmpty()) {
        return false
      }
      val mediaItem = MediaItem.Builder().setUri(File(videoModel.videoPath!!).toUri()).build()

      player = ExoPlayer.Builder(requireContext()).build()
      player!!.setMediaItem(mediaItem)
      player!!.addAnalyticsListener(EventLogger())
      player!!.setAudioAttributes(AudioAttributes.DEFAULT, true)
      player!!.playWhenReady = true
      playerView!!.player = player
    }
    player!!.prepare()
    return true
  }

  override fun onStart() {
    super.onStart()
    initializePlayer()
    if (playerView != null) {
      playerView!!.onResume()
    }
  }

  override fun onResume() {
    super.onResume()
    initializePlayer()
    if (playerView != null) {
      playerView!!.onResume()
    }
  }

  override fun onPause() {
    super.onPause()
    if (playerView != null) {
      playerView!!.onPause()
    }
    releasePlayer()
  }

  override fun onStop() {
    super.onStop()
    if (playerView != null) {
      playerView!!.onPause()
    }
    releasePlayer()
  }

  private fun releasePlayer() {
    if (player != null) {
      player!!.release()
      player = null
      playerView!!.player = null
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    unSubscribeUi()
  }

  private fun unSubscribeUi() {
    with(viewModel) {
      isCompleted.value = false
    }
  }
}