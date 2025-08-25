package com.muffinmanz.muff_secretphone.data.repository

import androidx.annotation.WorkerThread

import com.muffinmanz.muff_secretphone.APK_FOLDER
import com.muffinmanz.muff_secretphone.MUFF_CONST_APK
import com.muffinmanz.muff_secretphone.MUFF_CONST_UPLOAD
import com.muffinmanz.muff_secretphone.MUFF_FOLDER
import com.muffinmanz.muff_secretphone.PARSER
import com.muffinmanz.muff_secretphone.VIDEO_FOLDER
import com.muffinmanz.muff_secretphone.ZIP_FOLDER
import com.muffinmanz.muff_secretphone.data.db.*
import com.muffinmanz.muff_secretphone.data.model.Upload2560Model
import com.muffinmanz.muff_secretphone.data.model.UploadModel
import com.muffinmanz.muff_secretphone.network.MUFFClient
import com.muffinmanz.muff_secretphone.network.MUFFervice
import com.muffinmanz.muff_secretphone.network.ErrorResponseMapper
import com.muffinmanz.muff_secretphone.utilities.CommonUtils
import com.muffinmanz.muff_secretphone.utilities.DateUtil
import com.muffinmanz.muff_secretphone.utilities.EpsDbHelper
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

import java.io.File
import java.io.InputStream
import java.nio.channels.Channels
import java.text.SimpleDateFormat
import java.util.*

import javax.inject.Inject

import kotlin.collections.HashMap

class LoginRepository @Inject constructor(
    private val tbEpOswLogDao: TB_EP_OSW_LOGDao,
    private val ipConfigDao: IP_CONFIGDao,
    private val loginLogDao: LOGIN_LOGDao,
    private val tbEpOsw0070ctdao: TB_EP_OSW_0070CTDao,
    private val tbEpOsw0080ctdao: TB_EP_OSW_0080CTDao,
    private val tbEpOsw2110ntDao: TB_EP_OSW_2110NTDao,
    private val tbEpOsw2220ntDao: TB_EP_OSW_2220NTDao,
    private val tbEpOsw2230ntdao: TB_EP_OSW_2230NTDao,
    private val tbEpOsw2560ntdao: TB_EP_OSW_2560NTDao,
    private val videoRecDataDao: VideoRecDataDao,
    private val muffClient: MUFFClient,
    private val moshi: Moshi
) {
    @WorkerThread
    fun fetchDownload(
        percentUpdate: (Int) -> Unit,
        onStart: () -> Unit,
        onWarning: (String?) -> Unit,
        onError: (String?) -> Unit
    ) = flow {
        val zipFile = File(APK_FOLDER, "MUFF_V3.apk")
        val newUrl = CommonUtils.downloadUrl + "/" + MUFF_CONST_APK
        val response = MUFFervice.downloadCreate(percentUpdate).apkDownload(newUrl)

        response.suspendOnSuccess {
            data.byteStream().saveToFile(zipFile.absolutePath)
            emit(true)
        }.onError {
            map(ErrorResponseMapper) {
                onWarning(message)
            }
        }.onException {
            onError(message)
        }

        emit(true)
    }.onStart {
        onStart()
    }.flowOn(Dispatchers.IO)

    private fun InputStream.saveToFile(savePath: String) = use { input ->
        File(savePath).outputStream().use { output ->
            input.copyTo(output)

            val rbc = Channels.newChannel(input)
            output.channel.transferFrom(rbc, 0, 1024.toLong())
            output.close()
            input.close()
        }
    }

    @WorkerThread
    fun insertIpConfig() = flow {
        val config = IP_CONFIG(1, CommonUtils.uploadUrl, "업로드 URL")
        ipConfigDao.insertIpConfig(config)
        emit(true)
    }

    @WorkerThread
    fun insertLoginLog(user: String, rgstDt: String) = flow {
        val loginLog = LOGIN_LOG(loginLogDao.getMaxSeq() + 1, user, "log-in", rgstDt)
        loginLogDao.insertLoginLog(loginLog)
        emit(true)
    }

    @WorkerThread
    fun fetchTbEpOsw2110Nt() = flow {
        tbEpOsw2110ntDao.fetchTbEpOsw2110NtDataCheck() ?: return@flow

        val tbEpOsw2110Nt = tbEpOsw2110ntDao.fetchTbEpOsw2110Nt()
        val year = tbEpOsw2110Nt.oPRNYEAR
        val oprnTme = tbEpOsw2110Nt.oPRNTME
        val oprnTmeDTs = tbEpOsw2110Nt.oPRNTMEDTS

        val oprnTitle = "${year} 제 ${oprnTme}회 외국인력 선발포인트제 기능시험"

        val dbDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREAN)
        val infoDateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN)
        val onlyDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

        val startDe = tbEpOsw2110Nt.bGDE
        val newStartDate = infoDateFormat.format(dbDateFormat.parse(startDe) ?: "")
        val startDate = onlyDateFormat.format(dbDateFormat.parse(startDe) ?: "")

        val endDs = tbEpOsw2110Nt.eNDE
        val newEndDate = infoDateFormat.format(dbDateFormat.parse(endDs) ?: "")
        val endDate = onlyDateFormat.format(dbDateFormat.parse(endDs) ?: "")
        val examDateStr = "$newStartDate~$newEndDate"

        val calendar = Calendar.getInstance()
        val amPm = if (calendar.get(Calendar.AM_PM) == 0) "am" else "pm"
        val nowDate = DateUtil.getNowDate(DateUtil.DEFAULT_DATE_FORMAT)

        val hashMap = HashMap<String, String>()
        hashMap["oprnTitle"] = oprnTitle
        hashMap["oprnDate"] = if(nowDate <= startDate) {
            startDate
        } else if(nowDate >= endDate) {
            endDate
        } else {
            nowDate
        }
        hashMap["examDateStr"] = examDateStr
        hashMap["oprnTme"] = "$oprnTme"
        hashMap["oprnTmeDts"] = "$oprnTmeDTs"
        hashMap["amPm"] = amPm
        hashMap["startDate"] = startDate
        hashMap["endDate"] = endDate

        emit(hashMap)
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun onDeleteSignal() = flow {
        VIDEO_FOLDER.listFiles()?.forEach {
            if(it.extension.equals("mp4", true)) {
                videoRecDataDao.deleteVideoDb()
                it.deleteRecursively()
            }
        }
        videoRecDataDao.notUseVideo()

        emit(true)
    }

    @WorkerThread
    fun jsonFileUpload(
        evlDe: String,
        amPmSecd: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onWarning: (String?) -> Unit,
        onError: (String?) -> Unit
    ) = flow {
        //응결시 결과 업로드 시작
        val fetchVideoList = tbEpOsw2220ntDao.fetchVideoList(evlDe, amPmSecd)

        val mtbUploadList: MutableList<UploadModel> = mutableListOf()

        var count = 0
        fetchVideoList?.forEach { interViewer ->
            val crFileNm = videoRecDataDao.fetchVideoInfo(interViewer.examNo)?.videoPath?.let { File(it).name }
            val uploadModel = UploadModel(
                birth = interViewer.birth,
                birthId = "",
                businessName = interViewer.sectors,
                detailFieldName = interViewer.detailSectors,
                dtm = videoRecDataDao.fetchVideoInfo(interViewer.examNo)?.saveDate ?: "",
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
                vCheck = videoRecDataDao.fetchVideoInfo(interViewer.examNo)?.useAt ?: "",
                videoUrl = crFileNm ?: ""
            )
            mtbUploadList.add(uploadModel)
            count++
        }

        val listModel = Types.newParameterizedType(List::class.java, UploadModel::class.java)
        val adapter: JsonAdapter<List<UploadModel>> = moshi.adapter(listModel)
        val json = adapter.toJson(mtbUploadList)
        val jsonFile = File(MUFF_FOLDER, "interviewer.json")
        if (!jsonFile.exists()) jsonFile.createNewFile()
        jsonFile.writeText(json)

        val jsonFilePart = MultipartBody.Part.createFormData(
            "file",
            jsonFile.name,
            jsonFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        val newUrl = if(CommonUtils.uploadUrl.endsWith("/")) {
            CommonUtils.uploadUrl + MUFF_CONST_UPLOAD
        } else {
            CommonUtils.uploadUrl + "/" + MUFF_CONST_UPLOAD
        }

        val response = muffClient.jsonFileUpload(newUrl, jsonFilePart)
        response.suspendOnSuccess {
            emit(true)
        }.onError {
            map(ErrorResponseMapper) {
                onWarning(message)
            }
        }.onException {
            onError(message)
        }

        //특이사항 업로드 시작.
        val fetch2560List = tbEpOsw2560ntdao.fetchTbEpOsw2560List()
        val mtbUpload2560List: MutableList<Upload2560Model> = mutableListOf()
        count = 0
        fetch2560List?.forEach { item ->
            val upload2560Model = Upload2560Model(
                sPNTSEQ = item.sPNTSEQ,
                kLNGEXAMAYEXNO = item.kLNGEXAMAYEXNO,
                sPNTCTGRY = item.sPNTCTGRY,
                sPNTCN = item.sPNTCN,
                rGSTDT = item.rGSTDT,
                rGSRID = item.rGSRID,
                uPDTDT = item.uPDTDT,
                uPPSID = item.uPPSID,
                gRDRSEQ = item.gRDRSEQ
            )
            mtbUpload2560List.add(upload2560Model)
            count++
        }

        val list2560Model = Types.newParameterizedType(List::class.java, Upload2560Model::class.java)
        val adapter2560: JsonAdapter<List<Upload2560Model>> = moshi.adapter(list2560Model)
        val json2560 = adapter2560.toJson(mtbUpload2560List)
        val json2560File = File(MUFF_FOLDER, "result_2560.json")
        if(!json2560File.exists()) {
            json2560File.createNewFile()
        }
        json2560File.writeText(json2560)

        val json2560FilePart = MultipartBody.Part.createFormData(
            "file",
            json2560File.name,
            json2560File.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        val response2560 = muffClient.jsonFileUpload(newUrl, json2560FilePart)
        response2560.suspendOnSuccess {
            emit(true)
        }.onError {
            map(ErrorResponseMapper) {
                onWarning(message)
            }
        }.onException {
            onError(message)
        }
        //특이사항 업로드 끝.
    }.onStart {
        onStart()
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun videoFileUpload(
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onWarning: (String?) -> Unit,
        onError: (String?) -> Unit
    ) = flow {
        val fetchVideoRecList = videoRecDataDao.fetchVideoRecList()
        val fileBodyList: MutableList<MultipartBody.Part> = mutableListOf()

        fetchVideoRecList.forEach {
            val videoFile = File(it.videoPath)
            val videoPart = MultipartBody.Part.createFormData(
                "file",
                videoFile.name,
                videoFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
            fileBodyList.add(videoPart)
        }

        val newUrl = if (CommonUtils.uploadUrl.endsWith("/")) {
            CommonUtils.uploadUrl + MUFF_CONST_UPLOAD
        } else {
            CommonUtils.uploadUrl + "/" + MUFF_CONST_UPLOAD
        }

        val response = muffClient.videoFileUpload(newUrl, fileBodyList)
        response.suspendOnSuccess {
            emit(true)
        }.onError {
            map(ErrorResponseMapper) {
                onWarning(message)
            }
        }.onException {
            onError(message)
        }
    }.onStart {
        onStart()
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun insertExtDatabase(stepStr: (String) -> Unit, onError: (String?) -> Unit) = flow {
        ZIP_FOLDER.listFiles()?.forEach {
            if(it.name.endsWith(".sqlite")) {
                val dbHelper = EpsDbHelper(it.absolutePath)
                delay(100L)
                stepStr("0070CT 추출")
                tbEpOsw0070ctdao.insertTbEpOsw0070Dao(dbHelper.tbEpOsw0070Ct)
                delay(100L)
                stepStr("0080CT 추출")
                tbEpOsw0080ctdao.insertTbEpOsw0080Dao(dbHelper.tbEpOsw0080Ct)
                delay(100L)
                stepStr("2110NT 추출")
                tbEpOsw2110ntDao.insertTbEpOsw2110Dao(dbHelper.tbEpOsw2110Nt)
                delay(100L)
                stepStr("2220NT 추출")
                prt2220Nt(dbHelper) { outPrint ->
                    stepStr(outPrint)
                }
                delay(100L)
                stepStr("2230NT 추출")
                tbEpOsw2230ntdao.insertTbEpOsw2230Dao(dbHelper.tbEpOsw2230Nt)
            }
        }
        delay(100L)
        stepStr("추출 완료")
        emit(true)
    }.catch { e ->
        /* onError(e.message) */
    }.flowOn(Dispatchers.IO)

    private suspend fun prt2220Nt(helper: EpsDbHelper, outPrint: (String) -> Unit) {
        val hlperMap: MutableMap<Int, List<Long>> = mutableMapOf()
        val total = helper.tbEpOsw2220NtCount

        if(total > PARSER) {
            val divCount = total / PARSER
            val exmneList = helper.tbEpOsw2220NtExmneList

            for(i: Int in 1..divCount) {
                val jList: MutableList<Long> = mutableListOf()

                for(j: Int in ((i - 1) * PARSER) until (i * PARSER)) {
                    jList.add(exmneList[j])
                }
                hlperMap[i - 1] = jList
            }
            val iList: MutableList<Long> = mutableListOf()
            for(i: Int in (divCount * PARSER) until total) {
                iList.add(exmneList[i])
            }
            hlperMap[divCount] = iList

            for(i: Int in 0 until hlperMap.keys.size) {
                outPrint("2220NT 분할DB 작업 $i")
                val list = helper.tbEpOsw2220NtBetween(hlperMap[i]!!.first(), hlperMap[i]!!.last())
                tbEpOsw2220ntDao.insertTbEpOsw2220Dao(list)
                delay(1000L)
            }
        } else {
            tbEpOsw2220ntDao.insertTbEpOsw2220Dao(helper.tbEpOsw2220Nt)
        }
    }

    @WorkerThread
    fun fetchLogInfo(kind: String) = flow {
        val logInfo = tbEpOswLogDao.fetchLogInfo(kind)
        val log = if(logInfo != null) {
            "${logInfo.regDt} ${logInfo.content} "
        } else {
            ""
        }
        emit(log)
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun insertLog(kind: String, content: String, userId: String) {
        val logInfo = TB_EP_OSW_LOG(
            tbEpOswLogDao.getMaxLogId() + 1,
            kind,
            content,
            userId,
            DateUtil.getNowDate(null)
        )
        tbEpOswLogDao.insertLog(logInfo)
    }
}