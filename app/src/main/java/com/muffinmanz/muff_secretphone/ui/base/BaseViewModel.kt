package com.muffinmanz.muff_secretphone.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel : ViewModel() {

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> get() = _isLoading

  private val _warningMsg = MutableLiveData<String>()
  val warningMsg: LiveData<String> get() = _warningMsg

  private val _errorMsg = MutableLiveData<String>()
  val errorMsg: LiveData<String> get() = _errorMsg

  protected fun setLoading(value: Boolean) {
    _isLoading.value = value
  }
  protected fun setWarningMsg(msg: String?) {
    msg?.let {
      _warningMsg.postValue(it)
    }
  }

  protected fun setErrorMsg(msg: String?) {
    msg?.let {
      _errorMsg.postValue(it)
    }
  }
}