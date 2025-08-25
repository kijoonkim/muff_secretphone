package com.muffinmanz.muff_secretphone.ui.upload

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.muffinmanz.muff_secretphone.data.repository.UploadRepository
import com.muffinmanz.muff_secretphone.ui.base.BaseViewModel
import com.muffinmanz.muff_secretphone.ui.grader.GraderFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(private val repository: UploadRepository, private val state: SavedStateHandle) : BaseViewModel() {
  val args = GraderFragmentArgs.fromSavedStateHandle(state)
  val evlDe = args.selecDate
  val amPmSecd = args.selecAmPm

  val fetchVideoList = repository.fetchVideoList(evlDe, amPmSecd)

  val fetchOperTme = repository.fetchOperTme()

  val fetchAYeXynCount = repository.fetchAYeXynCount()

  fun fileUpload(evlDe: String, amPmSecd: String) = viewModelScope.launch {
    repository.fileUpload(
      evlDe = evlDe,
      amPmSecd = amPmSecd,
      onStart = { setLoading(true) },
      onComplete = { setLoading(false) },
      onWarning = { setWarningMsg(it) },
      onError = { setErrorMsg(it) }
    ).collectLatest {
      if (it)
        setWarningMsg("업로드 완료")
      else
        setWarningMsg("업로드 실패")
    }
  }
}