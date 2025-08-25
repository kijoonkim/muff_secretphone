package com.muffinmanz.muff_secretphone.data.repository

import androidx.annotation.WorkerThread

import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2110NTDao
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2220NTDao
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2560NT
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2560NTDao
import com.muffinmanz.muff_secretphone.data.model.HomeListModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import javax.inject.Inject

class HomeRepository @Inject constructor(
	private val tbEpOsw2110ntDao: TB_EP_OSW_2110NTDao,
	private val tbEpOsw2220ntDao: TB_EP_OSW_2220NTDao,
	private val tbEpOsw2560ntDao: TB_EP_OSW_2560NTDao
) {
	@WorkerThread
	fun fetchOprnTitle() = flow {
		tbEpOsw2110ntDao.fetchTbEpOsw2110NtDataCheck() ?: return@flow

		val tbEpOsw2110Nt = tbEpOsw2110ntDao.fetchTbEpOsw2110Nt()
		val year = tbEpOsw2110Nt.oPRNYEAR
		val oprnTme = tbEpOsw2110Nt.oPRNTME

		val oprnTitle = "${year} 제 ${oprnTme}회 외국인력 선발포인트제 기능시험"

		emit(oprnTitle)
	}.flowOn(Dispatchers.IO)

	@WorkerThread
	fun fetchOprnTmeDts() = flow {
		tbEpOsw2110ntDao.fetchTbEpOsw2110NtDataCheck() ?: return@flow

		val tbEpOsw2110Nt = tbEpOsw2110ntDao.fetchTbEpOsw2110Nt()
		val oprnTme = tbEpOsw2110Nt.oPRNTME
		val oprnTmeDTs = tbEpOsw2110Nt.oPRNTMEDTS

		val oprnTmeDts = "세부회차: ${oprnTme}-${oprnTmeDTs}"

		emit(oprnTmeDts)
	}.flowOn(Dispatchers.IO)

	@WorkerThread
	fun fetchRcptCount(evlDe: String, amPmSecd: String) = flow {
		emit(tbEpOsw2220ntDao.fetchRcptCount(evlDe, amPmSecd) ?: "0")
	}.flowOn(Dispatchers.IO)

	fun fetchAplyCount(evlDe: String, amPmSecd: String) = tbEpOsw2220ntDao.fetchAplyCount(evlDe, amPmSecd)

	fun fetchAbsentCount(evlDe: String, amPmSecd: String) = tbEpOsw2220ntDao.fetchAbsentCount(evlDe, amPmSecd)

	fun fetchFlowInterViewerList(evlDe: String, amPmSecd: String) = tbEpOsw2220ntDao.fetchFlowInterViewerList(evlDe, amPmSecd)

	fun fetchInterViewerList(map: HashMap<String, String>, onWarning: (String?) -> Unit) = flow {
		val evlDe = map["evlDe"].toString()
		val amPmSecd = map["amPmSecd"].toString()
		val list: List<HomeListModel>? = if(map["qNum"].isNullOrEmpty() && map["name"].isNullOrEmpty() && map["birth"].isNullOrEmpty() && map["sector"].isNullOrEmpty() && map["detailSector"].isNullOrEmpty()) {
            val nullList = tbEpOsw2220ntDao.fetchInterViewerList(
                evlDe,
                amPmSecd,
                sector = map["sector"],
                detailSector = map["detailSector"]
            )

            if(nullList != null) {
                if(nullList.size == 0) {
                    onWarning("조회된 응시자가 없습니다.")
                }
            }

            nullList
        } else {
            val getList = tbEpOsw2220ntDao.fetchInterViewerList(
                evlDe,
                amPmSecd,
                offNumber = map["qNum"],
                name = map["name"],
                birth = map["birth"],
                sector = map["sector"],
                detailSector = map["detailSector"]
            )

            if(getList != null) {
                if(getList.size == 0) {
                    onWarning("조회된 응시자가 없습니다.")
                }
            }

            getList
        }

		emit(list)
	}.flowOn(Dispatchers.IO)

	suspend fun updateGender(
		sex: String,
		offNum: String,
		examNo: String,
		skillNum: String,
		evlDe: String,
		amPmSecd: String,
		onWarning: (String?) -> Unit
	) = flow {
		val nextGender = tbEpOsw2220ntDao.updateUserGender(sex, examNo, offNum, skillNum, evlDe, amPmSecd)
		onWarning("성별이 변경되었습니다.")

		if(nextGender == null) {
			val currentTimestamp = System.currentTimeMillis()
			val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
			val currentDate = format.format(Date(currentTimestamp))

			val nextSpntSeq = tbEpOsw2560ntDao.getMaxSpntSeq() + 1

			val sPNTCN = if (sex == "21201") "여자에서 남자" else "남자에서 여자"

			val newEntry = TB_EP_OSW_2560NT(
				sPNTSEQ = nextSpntSeq,
				kLNGEXAMAYEXNO = examNo,
				sPNTCTGRY = "SXDI",
				sPNTCN = sPNTCN,
				rGSTDT = currentDate,
				rGSRID = "admin",
				uPDTDT = currentDate,
				uPPSID = "admin",
				gRDRSEQ = "0"
			)
			tbEpOsw2560ntDao.insertTbEpOsw2560Dao(listOf(newEntry))
		} else {
			emit(nextGender)
		}
	}

	suspend fun updateICheck(
		status: String,
		offNum: String,
		examNo: String,
		skillNum: String,
		evlDe: String,
		amPmSecd: String,
		onWarning: (String?) -> Unit
	) = flow {
		val nextHomeModel = tbEpOsw2220ntDao.updateICheckAndNext(status, examNo, offNum, skillNum, evlDe, amPmSecd)

		if(nextHomeModel == null) {
			onWarning("마지막 응시자 입니다.")
		} else {
			emit(nextHomeModel)
		}
	}

	suspend fun fetchNext(examNo: String, skillNum: String, evlDe: String, amPmSecd: String, onWarning: (String?) -> Unit) = flow {
		val nextResult = tbEpOsw2220ntDao.fetchNextUser(examNo, skillNum, evlDe, amPmSecd)

		if(nextResult == null) {
			onWarning("마지막 페이지 입니다.")
		} else {
			emit(nextResult)
		}
	}.flowOn(Dispatchers.IO)

	suspend fun fetchPrev(examNo: String, skillNum: String, evlDe: String, amPmSecd: String, onWarning: (String?) -> Unit) = flow {
		val prevResult = tbEpOsw2220ntDao.fetchPrevUser(examNo, skillNum, evlDe, amPmSecd)

		if(prevResult == null) {
			onWarning("첫번째 페이지 입니다.")
		} else {
			emit(prevResult)
		}
	}.flowOn(Dispatchers.IO)

	@WorkerThread
	suspend fun fetchSectorsList(selectedDetailSector: String): List<String> {
		return tbEpOsw2220ntDao.fetchSectorsList(selectedDetailSector) ?: emptyList()
	}

	@WorkerThread
	suspend fun fetchAllSectorsList(): List<String> {
		return tbEpOsw2220ntDao.fetchAllSectorsList() ?: emptyList()
	}

	@WorkerThread
	suspend fun fetchDetailSectorsList(selectedSector: String): List<String> {
		return tbEpOsw2220ntDao.fetchDetailSectorsList(selectedSector) ?: emptyList()
	}

	@WorkerThread
	suspend fun fetchAllDetailSectorsList(): List<String> {
		return tbEpOsw2220ntDao.fetchAllDetailSectorsList() ?: emptyList()
	}
}