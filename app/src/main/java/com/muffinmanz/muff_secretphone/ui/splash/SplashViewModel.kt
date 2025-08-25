package com.muffinmanz.muff_secretphone.ui.splash

import androidx.lifecycle.viewModelScope
import com.muffinmanz.muff_secretphone.data.repository.SplashRepository
import com.muffinmanz.muff_secretphone.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: SplashRepository): BaseViewModel() {

  val nextSignal = MutableStateFlow(false)

  fun updateIpConfig() = viewModelScope.launch {
    repository.fetchIpConfig().collectLatest {
      nextSignal.value = it
    }
  }
}