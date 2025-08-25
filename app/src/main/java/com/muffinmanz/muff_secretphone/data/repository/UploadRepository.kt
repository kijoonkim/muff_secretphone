package com.muffinmanz.muff_secretphone.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.muffinmanz.muff_secretphone.MUFF_CONST_MAPPING
import com.muffinmanz.muff_secretphone.MUFF_CONST_UPLOAD
import com.muffinmanz.muff_secretphone.MUFF_FOLDER
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2110NTDao
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2220NTDao
import com.muffinmanz.muff_secretphone.data.db.VideoRecData
import com.muffinmanz.muff_secretphone.data.db.VideoRecDataDao
import com.muffinmanz.muff_secretphone.data.model.UploadModel
import com.muffinmanz.muff_secretphone.network.MUFFClient
import com.muffinmanz.muff_secretphone.network.ErrorResponseMapper
import com.muffinmanz.muff_secretphone.utilities.CommonUtils
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class UploadRepository @Inject constructor(
  private val tbEpOsw2110ntDao: TB_EP_OSW_2110NTDao,
  private val tbEpOsw2220ntDao: TB_EP_OSW_2220NTDao,
  private val videoRecDataDao: VideoRecDataDao,
  private val muffClient: MUFFClient,
  private val moshi: Moshi
) {

  @WorkerThread
  fun fetchVideoList(evlDe: String, amPmSecd: String) = flow {
    val vList = tbEpOsw2220ntDao.fetchVideoList(evlDe, amPmSecd)
    if (vList.isNullOrEmpty()) return@flow
    else {
      vList.forEach {
        val vInfo = videoRecDataDao.fetchVideoInfo(it.examNo)

        if (vInfo != null) {
          it.vCheck = "완료"
          it.videoPath = vInfo.videoPath
        } else {
          it.vCheck = "-"
        }
      }
      emit(vList)
    }
  }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun fetchOperTme() = flow {
    tbEpOsw2110ntDao.fetchTbEpOsw2110NtDataCheck() ?: return@flow

    val tbEpOsw2110Nt = tbEpOsw2110ntDao.fetchTbEpOsw2110Nt()
    val dbTme = tbEpOsw2110Nt.oPRNTME
    val oprnTme = "${dbTme}차"

    emit(oprnTme)
  }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun fetchAYeXynCount() = flow {
    val count = tbEpOsw2220ntDao.aYeXynCount()
    emit("${count}명")
  }

  @WorkerThread
  fun fileUpload(evlDe: String, amPmSecd: String, onStart: () -> Unit, onComplete: () -> Unit, onWarning: (String?) -> Unit, onError: (String?) -> Unit) = flow {
    val fetchVideoRecList = videoRecDataDao.fetchVideoRecList()

    val fetchVideoList = tbEpOsw2220ntDao.fetchVideoList(evlDe, amPmSecd)

    val mtbUploadList: MutableList<UploadModel> = mutableListOf()

    var count = 0
    fetchVideoList?.forEach { interViewer ->
      fetchVideoRecList.forEach { rec ->
        val uploadModel = UploadModel(
          birth = interViewer.birth,
          birthId = "",
          businessName = interViewer.sectors,
          detailFieldName = interViewer.detailSectors,
          dtm = if (rec.examNo == interViewer.examNo) rec.saveDate else "",
          evaluatorsName = "관리자",
          examTestNumber = interViewer.examNo,
          gender = interViewer.sex,
          genderChanged = false,
          iCheck = interViewer.iCheck ?: "",
          id = count,
          imageOffCheck = false,
          imageUrl = "",
          loginId = "",
          name = interViewer.name,
          number = interViewer.offNumber,
          vCheck = if (rec.examNo == interViewer.examNo) "Y" else "N",
          videoUrl = interViewer.videoPath ?: ""
        )
        mtbUploadList.add(uploadModel)
      }
      count++
    }

    val listModel = Types.newParameterizedType(List::class.java, UploadModel::class.java)
    val adapter: JsonAdapter<List<UploadModel>> = moshi.adapter(listModel)
    val json = adapter.toJson(mtbUploadList)
    val jsonFile = File(MUFF_FOLDER, "interviewer.json")
    if(!jsonFile.exists()) jsonFile.createNewFile()
    jsonFile.writeText(json)

    val fileBodyMap: HashMap<String, RequestBody> = HashMap()

    fileBodyMap["interviewer"] = jsonFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())

    fetchVideoRecList.forEach {
      fileBodyMap[it.examNo] = File(it.videoPath).asRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    val newUrl = if (CommonUtils.uploadUrl.endsWith("/")) {
      CommonUtils.uploadUrl + MUFF_CONST_UPLOAD
    } else {
      CommonUtils.uploadUrl + "/" + MUFF_CONST_UPLOAD
    }

   // val response = muffClient.videoFileUpload(newUrl, fileBodyMap)
   // response.suspendOnSuccess {
      emit(true)
  //  }.onError { map(ErrorResponseMapper) { onWarning(message) } }.onException { onError(message) }
  }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)
}