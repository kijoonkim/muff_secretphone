package com.muffinmanz.muff_secretphone.ui.download

import androidx.lifecycle.viewModelScope
import com.muffinmanz.muff_secretphone.LOG_KIND_003
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.data.repository.DownloadRepository
import com.muffinmanz.muff_secretphone.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(private val repository: DownloadRepository) : BaseViewModel() {

  val stepStringFlow = MutableStateFlow("")
  val isComplete = MutableStateFlow(false)
  val isWorking = MutableStateFlow(false)

  fun insertDownloadUrl() = viewModelScope.launch {
    repository.insertDownloadUrl().collect()
  }

  fun fetchDownload(percentUpdate: (Int) -> Unit) = viewModelScope.launch {
    repository.fetchDownload(
      percentUpdate = percentUpdate,
      onStart = { stepStringFlow.value = "연결중..."; isWorking.value = true },
      onWarning = { setWarningMsg(it); stepStringFlow.value = it.toString(); isWorking.value = false },
      onError = { setErrorMsg(it); stepStringFlow.value = it.toString(); isWorking.value = false }
    ).collectLatest {
      insertExtDatabase()
    }
  }

  private fun insertExtDatabase() = viewModelScope.launch {
    isWorking.value = true
    repository.insertExtDatabase(
      stepStr = { stepStringFlow.value = it },
      onError = { setErrorMsg(it); stepStringFlow.value = it.toString(); isWorking.value = false }
    ).collectLatest {
      isComplete.value = it
      isWorking.value = false
    }
  }

  fun insertLog(kind:String, content: String) = viewModelScope.launch {
    repository.insertLog(kind, content, "")
  }
}