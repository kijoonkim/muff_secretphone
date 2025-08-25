package com.muffinmanz.muff_secretphone.ui.videoresult

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muffinmanz.muff_secretphone.data.db.VideoRecData
import com.muffinmanz.muff_secretphone.data.repository.VideoResultRepository
import com.muffinmanz.muff_secretphone.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoResultViewModel @Inject constructor(private val repository: VideoResultRepository) : BaseViewModel() {

  val isCompleted = MutableStateFlow(false)
  fun updateVideoRecInfo(video: VideoRecData) = viewModelScope.launch {
    repository.updateVideoRecInfo(video)
    isCompleted.value = true
  }
}