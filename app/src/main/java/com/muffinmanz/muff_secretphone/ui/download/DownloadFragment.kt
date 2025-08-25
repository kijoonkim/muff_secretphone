package com.muffinmanz.muff_secretphone.ui.download

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.databinding.FragmentDownloadBinding
import com.muffinmanz.muff_secretphone.extensions.observe
import com.muffinmanz.muff_secretphone.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.muffinmanz.muff_secretphone.LOG_KIND_003

@AndroidEntryPoint
class DownloadFragment : BaseFragment<FragmentDownloadBinding, DownloadViewModel>(R.layout.fragment_download) {

  override val viewModel: DownloadViewModel by viewModels()

  override fun init() {
    viewModel.insertDownloadUrl()

    subscribeUi()

    binding.apply {

      btnBottom.setOnClickListener {

        if (!viewModel.isComplete.value) {
          viewModel.fetchDownload { percent ->
            lifecycleScope.launch {
              uploadProgress.progress = percent
              tvResultInfo.text = StringBuilder("$percent/100")
            }
          }
        } else {
          findNavController().popBackStack()
        }
      }

      llBackPress.setOnClickListener {
        findNavController().popBackStack()
      }
    }
  }

  private fun subscribeUi() {
    with(viewModel) {
      stepStringFlow.observe(viewLifecycleOwner, ::fetchStepStr)
      isComplete.observe(viewLifecycleOwner, ::fetchIsCompleted)
      isWorking.observe(viewLifecycleOwner, ::fetchIsWorking)
    }
  }

  private fun fetchIsWorking(isWorking: Boolean) {
    binding.btnBottom.isEnabled = !isWorking
  }

  private fun fetchIsCompleted(isComp: Boolean) {
    if (isComp) {
      binding.btnBottom.text = "확인"
      //응시정보 다운로드 로그 기록.
      viewModel.insertLog(LOG_KIND_003, requireContext().getString(R.string.hq_server_sync_log))
    } else binding.btnBottom.text = "다운로드"
  }

  private fun fetchStepStr(step: String) {
    binding.tvResultInfo.text = step
  }

  override fun onDestroyView() {
    super.onDestroyView()
    unSubscribeUi()
  }

  private fun unSubscribeUi() {
    with(viewModel) {
      isComplete.value = false
    }
  }
}