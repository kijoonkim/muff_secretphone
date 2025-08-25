package com.muffinmanz.muff_secretphone.ui.grader

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope

import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import com.muffinmanz.muff_secretphone.data.repository.GraderRepository
import com.muffinmanz.muff_secretphone.ui.base.BaseViewModel

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class GraderViewModel @Inject constructor(
    private val repository: GraderRepository,
    private val state: SavedStateHandle
) : BaseViewModel() {
    val args = GraderFragmentArgs.fromSavedStateHandle(state)
    val evlDe = args.selecDate
    val amPmSecd = args.selecAmPm

    val fetchVideoList = MutableLiveData<List<VideoListModel>?>()
    val sectorsList = MutableLiveData<List<String>>()
    val detailSectorsList = MutableLiveData<List<String>>()

    init {
        viewModelScope.launch {
            repository.fetchFlowVideoList(evlDe, amPmSecd).collectLatest {
                fetchVideoList.postValue(it)
            }
        }
    }

    fun fetchFlowVideoList() {
        viewModelScope.launch {
            repository.fetchFlowVideoList(evlDe, amPmSecd).collectLatest {
                fetchVideoList.postValue(it)
            }
        }
    }

    fun fetchFlowVideoListSorted() {
        viewModelScope.launch {
            repository.fetchFlowVideoListSorted(evlDe, amPmSecd).collectLatest {
                fetchVideoList.postValue(it)
            }
        }
    }

    fun search(map: HashMap<String, String>) = viewModelScope.launch {
        repository.fetchVideoList(
            map,
            onWarning = { setWarningMsg(it) }
        ).collectLatest {
            fetchVideoList.postValue(it)
        }
    }

    fun fetchSectorsList(selectedDetailSector: String?) {
        viewModelScope.launch {
            val sectors = if (selectedDetailSector == null) {
                repository.fetchAllSectorsList()
            } else {
                repository.fetchSectorsList(selectedDetailSector)
            }
            sectorsList.postValue(sectors)
        }
    }

    fun fetchDetailSectorsList(selectedSector: String?) {
        viewModelScope.launch {
            val detailSectors = if (selectedSector == null) {
                repository.fetchAllDetailSectorsList()
            } else {
                repository.fetchDetailSectorsList(selectedSector)
            }
            detailSectorsList.postValue(detailSectors)
        }
    }
}