package com.muffinmanz.muff_secretphone.data.repository

import androidx.annotation.WorkerThread

import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2220NTDao
import com.muffinmanz.muff_secretphone.data.model.VideoListModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

import javax.inject.Inject

class GraderRepository @Inject constructor(private val tbEpOsw2220ntDao: TB_EP_OSW_2220NTDao) {
    fun fetchFlowVideoList(evlDe: String, amPmSecd: String) = tbEpOsw2220ntDao.fetchFlowVideoList(evlDe, amPmSecd)
    fun fetchFlowVideoListSorted(evlDe: String, amPmSecd: String) = tbEpOsw2220ntDao.fetchFlowVideoListSorted(evlDe, amPmSecd)

    fun fetchVideoList(map: HashMap<String, String>, onWarning: (String?) -> Unit) = flow {
        val evlDe = map["evlDe"].toString()
        val amPmSecd = map["amPmSecd"].toString()
        val vCheck = map["vCheck"]?.let { if (it == "완료") "완료" else "대기" }
        val list: List<VideoListModel>? = if (
            map["qNum"].isNullOrEmpty() &&
            map["name"].isNullOrEmpty() &&
            map["sector"].isNullOrEmpty() &&
            map["detailSector"].isNullOrEmpty()
        ) {
            val nullList = tbEpOsw2220ntDao.fetchVideoList(
                evlDe,
                amPmSecd,
                sector = map["sector"],
                detailSector = map["detailSector"],
                vCheck = vCheck
            )

            if(nullList != null) {
                if(nullList.size == 0) {
                    onWarning("조회된 응시자가 없습니다.")
                }
            }

            nullList
        } else {
            val getList = tbEpOsw2220ntDao.fetchVideoList(
                evlDe,
                amPmSecd,
                offNumber = map["qNum"],
                name = map["name"],
                sector = map["sector"],
                detailSector = map["detailSector"],
                vCheck = vCheck
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