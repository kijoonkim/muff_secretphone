package com.muffinmanz.muff_secretphone.ui.login

import androidx.lifecycle.viewModelScope

import com.muffinmanz.muff_secretphone.LOG_KIND_003
import com.muffinmanz.muff_secretphone.LOG_KIND_004
import com.muffinmanz.muff_secretphone.data.repository.LoginRepository
import com.muffinmanz.muff_secretphone.ui.base.BaseViewModel

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository) : BaseViewModel() {
    val fetchTbEpOsw2110Nt = repository.fetchTbEpOsw2110Nt()

    fun insertIpConfig() = viewModelScope.launch {
        repository.insertIpConfig().collect()
    }

    fun insertLoginLog(user: String, rgstDt: String) = viewModelScope.launch {
        repository.insertLoginLog(user, rgstDt).collect()
    }

    fun deleteHistory() = viewModelScope.launch {
        repository.onDeleteSignal().collectLatest {
            setWarningMsg("삭제가 완료되었습니다.")
        }
    }

    val updateLog = MutableStateFlow("")
    val uploadLog = MutableStateFlow("")
    val stepStringFlow = MutableStateFlow("")
    val isComplete = MutableStateFlow(false)
    val isWorking = MutableStateFlow(false)

    init {
        updateLogInfo()
    }

    fun updateLogInfo() {
        viewModelScope.launch {
            repository.fetchLogInfo(LOG_KIND_003).collectLatest {
                updateLog.value = it
            }

            repository.fetchLogInfo(LOG_KIND_004).collectLatest {
                uploadLog.value = it
            }
        }
    }

    fun fetchDownload(percentUpdate: (Int) -> Unit) = viewModelScope.launch {
        repository.fetchDownload(
            percentUpdate = percentUpdate,
            onStart = { setLoading(true) },
            onWarning = {
                setWarningMsg(it);
                stepStringFlow.value = it.toString();
                setLoading(false)
            },
            onError = {
                setErrorMsg(it);
                stepStringFlow.value = it.toString();
                setLoading(false)
            }
        ).collectLatest {
            setWarningMsg("APK 다운로드 완료\n다운로드 폴더에서 확인해주세요.");
            setLoading(false)
        }
    }

    fun jsonFileUpload(evlDe: String, amPmSecd: String, message: String) = viewModelScope.launch {
        if(evlDe == "") {
            setWarningMsg("시행일을 선택 하세요.")
        } else if(amPmSecd == "") {
            setWarningMsg("세션을 선택 하세요.")
        } else {
            repository.jsonFileUpload(
                evlDe = evlDe,
                amPmSecd = amPmSecd,
                onStart = { setLoading(true) },
                onComplete = { setLoading(false) },
                onWarning = { setWarningMsg(it); setLoading(false) },
                onError = { setErrorMsg(it); setLoading(false) }
            ).collectLatest {
                if(it) {
                    insertLog(LOG_KIND_004, message)
                    repository.fetchLogInfo(LOG_KIND_004).collectLatest {
                        uploadLog.value = it
                    }
                    setWarningMsg("업로드 완료\n영상파일은 USB를 통하여 옮겨주세요.")
                } else {
                    setWarningMsg("업로드 실패")
                }
            }
        }
    }

    fun videoFileUpload() = viewModelScope.launch {
        repository.videoFileUpload(
            onStart = { setLoading(true) },
            onComplete = { setLoading(false) },
            onWarning = { setWarningMsg(it) },
            onError = { setErrorMsg(it) }
        ).collectLatest {
            if(it) {
                setWarningMsg("업로드 완료")
            } else {
                setWarningMsg("업로드 실패")
            }
        }
    }

    fun insertExtDatabase() = viewModelScope.launch {
        isWorking.value = true
        repository.insertExtDatabase(
            stepStr = { stepStringFlow.value = it },
            onError = {
                setErrorMsg(it);
                stepStringFlow.value = it.toString();
                isWorking.value = false
            }
        ).collectLatest {
            isComplete.value = it
            isWorking.value = false

            if(it) {
                setWarningMsg("DB 업데이트 완료")
            }
            else {
                setWarningMsg("DB 업데이트 실패")
            }
        }
    }

    fun insertLog(kind: String, content: String) = viewModelScope.launch {
        repository.insertLog(kind, content, "")
    }
}