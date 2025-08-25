package com.muffinmanz.muff_secretphone.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope

import com.muffinmanz.muff_secretphone.data.model.HomeListModel
import com.muffinmanz.muff_secretphone.data.repository.HomeRepository
import com.muffinmanz.muff_secretphone.ui.base.BaseViewModel

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val state: SavedStateHandle
) : BaseViewModel() {
	val args = HomeFragmentArgs.fromSavedStateHandle(state)
	val evlDe = args.selecDate
	val amPmSecd = args.selecAmPm

	/* 홈 화면 */
	val fetchOprnTitle = MutableStateFlow("")
	val fetchOprnTmeDts = MutableStateFlow("")
	val fetchRcptCount = MutableStateFlow("")
	val fetchAplyCount = MutableStateFlow("")
	val fetchAbsentCount = MutableStateFlow("")

	val sectorsList = MutableLiveData<List<String>>()
	val detailSectorsList = MutableLiveData<List<String>>()

	init {
		viewModelScope.launch {
			repository.fetchOprnTitle().collectLatest {
				fetchOprnTitle.value = it
			}
		}
		viewModelScope.launch {
			repository.fetchOprnTmeDts().collectLatest {
				fetchOprnTmeDts.value = it
			}
		}
		viewModelScope.launch {
			repository.fetchRcptCount(evlDe, amPmSecd).collectLatest {
				fetchRcptCount.value = "${it}명"
			}
		}
		viewModelScope.launch {
			repository.fetchAplyCount(evlDe, amPmSecd).collectLatest {
				fetchAplyCount.value = "${it}명"
			}
		}
		viewModelScope.launch {
			repository.fetchAbsentCount(evlDe, amPmSecd).collectLatest {
				fetchAbsentCount.value = "${it}명"
			}
		}
		viewModelScope.launch {
			repository.fetchFlowInterViewerList(evlDe, amPmSecd).collectLatest {
				fetchInterViewerList.value = it
			}
		}
	}

	val fetchInterViewerList = MutableLiveData<List<HomeListModel>?>()

	fun search(map: HashMap<String, String>) = viewModelScope.launch {
		repository.fetchInterViewerList(
            map,
            onWarning = { setWarningMsg(it) }
        ).collectLatest {
			fetchInterViewerList.postValue(it)
		}
	}

	/* 다이얼로그 상태창 */
	val fetchOffNum = MutableStateFlow("")
	val fetchSkillNum = MutableStateFlow("")

	val fetchModel = MutableLiveData<HomeListModel>()

	fun updateGender(sex: String, updtExamNo: String) = viewModelScope.launch {
		repository.updateGender(
			sex = sex,
			offNum = fetchOffNum.value,
			examNo = updtExamNo,
			skillNum = fetchSkillNum.value,
			evlDe = evlDe,
			amPmSecd = amPmSecd,
			onWarning = { setWarningMsg(it) }
		).collectLatest {
			fetchModel.postValue(it)
			fetchSkillNum.value = it.skillNum
		}
	}

	fun updateICheck(status: String, updtExamNo: String, evlDe: String, amPmSecd: String) = viewModelScope.launch {
		repository.updateICheck(
			status = status,
			offNum = fetchOffNum.value,
			examNo = updtExamNo,
			skillNum = fetchSkillNum.value,
			evlDe = evlDe,
			amPmSecd = amPmSecd,
			onWarning = { setWarningMsg(it) }
		).collectLatest {
			fetchModel.postValue(it)
			fetchOffNum.value = it.offNumber
			fetchSkillNum.value = it.skillNum
		}
	}

	fun fetchNext(evlDe: String, amPmSecd: String) = viewModelScope.launch {
		repository.fetchNext(
			evlDe = evlDe,
			amPmSecd = amPmSecd,
			examNo = fetchOffNum.value,
			skillNum = fetchSkillNum.value,
			onWarning = { setWarningMsg(it) }
		).collectLatest {
			fetchModel.postValue(it)
			fetchOffNum.value = it.offNumber
			fetchSkillNum.value = it.skillNum
		}
	}

	fun fetchPrev(evlDe: String, amPmSecd: String) = viewModelScope.launch {
		repository.fetchPrev(
			evlDe = evlDe,
			amPmSecd = amPmSecd,
			examNo = fetchOffNum.value,
			skillNum = fetchSkillNum.value,
			onWarning = { setWarningMsg(it) }
		).collectLatest {
			fetchModel.postValue(it)
			fetchOffNum.value = it.offNumber
			fetchSkillNum.value = it.skillNum
		}
	}

	fun fetchSectorsList(selectedDetailSector: String?) {
		viewModelScope.launch {
			val sectors = if(selectedDetailSector == null) {
				repository.fetchAllSectorsList()
			} else {
				repository.fetchSectorsList(selectedDetailSector)
			}
			sectorsList.postValue(sectors)
		}
	}

	fun fetchDetailSectorsList(selectedSector: String?) {
		viewModelScope.launch {
			val detailSectors = if(selectedSector == null) {
				repository.fetchAllDetailSectorsList()
			} else {
				repository.fetchDetailSectorsList(selectedSector)
			}
			detailSectorsList.postValue(detailSectors)
		}
	}
}