package com.muffinmanz.muff_secretphone.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.muffinmanz.muff_secretphone.MainActivity
import com.muffinmanz.muff_secretphone.dialog.SimpleDialog
import com.muffinmanz.muff_secretphone.extensions.observe

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel>(@LayoutRes val layoutId: Int) : Fragment() {

  protected lateinit var binding: B
  protected abstract val viewModel: VM

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    observe(viewModel.warningMsg, ::showSimpleDialog)
    observe(viewModel.errorMsg, ::showSimpleDialog)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
    context ?: return binding.root

    viewModel.isLoading.observe(viewLifecycleOwner, ::showLoading)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.lifecycleOwner = this
    init()
  }

  abstract fun init()

  fun showToast(str: String) {
    Toast.makeText(requireContext(), str, Toast.LENGTH_LONG).show()
  }

  private fun showLoading(isLoading: Boolean) {
    MainActivity.loading.value = isLoading
  }

  private fun showSimpleDialog(msg: String) {
    val simpleDialog = SimpleDialog(requireContext(), msg)
    if (!SimpleDialog.showCheck) simpleDialog.show()
  }

}