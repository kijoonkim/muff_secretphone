package com.muffinmanz.muff_secretphone.ui.upload

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import com.muffinmanz.muff_secretphone.databinding.FragmentUploadBinding
import com.muffinmanz.muff_secretphone.extensions.observe
import com.muffinmanz.muff_secretphone.ui.adapter.ResultContentAdapter
import com.muffinmanz.muff_secretphone.ui.base.BaseFragment
import com.muffinmanz.muff_secretphone.ui.home.HomeFragmentArgs
import com.muffinmanz.muff_secretphone.utilities.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class UploadFragment : BaseFragment<FragmentUploadBinding, UploadViewModel>(R.layout.fragment_upload) {

  override val viewModel: UploadViewModel by viewModels()
  private val args: HomeFragmentArgs by navArgs()
  private val adapter = ResultContentAdapter()

  override fun init() {
    subscribeUi()
    setDateTime()
    binding.rvResultContent.adapter = adapter
    binding.btnCancel.setOnClickListener {
      navigateToGrader()
    }
    binding.btnSubmit.setOnClickListener {
      viewModel.fileUpload(args.selecDate, args.selecAmPm)
    }
    binding.tvIpInfo.addTextChangedListener {
      CommonUtils.uploadUrl = it.toString()
    }
  }

  private fun setDateTime() {
    val sdf = SimpleDateFormat("yyyy-mm-dd", Locale.KOREA)
    val dateString = sdf.format(System.currentTimeMillis())
    binding.tvResultDateInfo.text = dateString
  }

  private fun subscribeUi() {
    with(viewModel) {
      fetchVideoList.observe(viewLifecycleOwner, ::fetchVideoList)
      fetchOperTme.observe(viewLifecycleOwner, ::fetchOperTme)
      fetchAYeXynCount.observe(viewLifecycleOwner, ::fetchAYeXynCount)
    }
  }

  private fun fetchVideoList(list: List<VideoListModel>) {
    adapter.submitList(list)
  }

  private fun fetchOperTme(tme: String) {
    binding.tvDivisionInfo.text = tme
  }

  private fun fetchAYeXynCount(countStr: String) {
    binding.tvResultTotalInfo.text = countStr
  }

  private fun navigateToGrader() {
    findNavController().popBackStack()
  }
}