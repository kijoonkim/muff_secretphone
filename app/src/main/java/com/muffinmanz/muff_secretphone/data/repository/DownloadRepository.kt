package com.muffinmanz.muff_secretphone.data.repository

import androidx.annotation.WorkerThread
import com.muffinmanz.muff_secretphone.MUFF_CONST_MAPPING
import com.muffinmanz.muff_secretphone.MUFF_FOLDER
import com.muffinmanz.muff_secretphone.PARSER
import com.muffinmanz.muff_secretphone.ZIP_FOLDER
import com.muffinmanz.muff_secretphone.data.db.IP_CONFIG
import com.muffinmanz.muff_secretphone.data.db.IP_CONFIGDao
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_0070CTDao
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_0080CTDao
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2110NTDao
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2220NTDao
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2230NTDao
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_LOG
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_LOGDao
import com.muffinmanz.muff_secretphone.data.db.VideoRecDataDao
import com.muffinmanz.muff_secretphone.network.MUFFervice
import com.muffinmanz.muff_secretphone.network.ErrorResponseMapper
import com.muffinmanz.muff_secretphone.utilities.CommonUtils
import com.muffinmanz.muff_secretphone.utilities.DateUtil
import com.muffinmanz.muff_secretphone.utilities.EpsDbHelper
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import java.io.File
import java.io.InputStream
import java.nio.channels.Channels
import java.util.zip.ZipFile
import javax.inject.Inject

class DownloadRepository @Inject constructor(
  private val ipConfigDao: IP_CONFIGDao,
  private val tbEpOsw0070ctdao: TB_EP_OSW_0070CTDao,
  private val tbEpOsw0080ctdao: TB_EP_OSW_0080CTDao,
  private val tbEpOsw2110ntDao: TB_EP_OSW_2110NTDao,
  private val tbEpOsw2220ntdao: TB_EP_OSW_2220NTDao,
  private val tbEpOsw2230ntdao: TB_EP_OSW_2230NTDao,
  private val videoRecDataDao: VideoRecDataDao,
  private val tbEpOswLogDao: TB_EP_OSW_LOGDao
) {

  @WorkerThread
  fun insertDownloadUrl() = flow {
    val config = IP_CONFIG(0, CommonUtils.downloadUrl, "다운로드 URL 저장")
    ipConfigDao.insertIpConfig(config)
    emit(true)
  }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun fetchDownload(
    percentUpdate: (Int) -> Unit,
    onStart: () -> Unit,
    onWarning: (String?) -> Unit,
    onError: (String?) -> Unit
  ) = flow {
    // 기존에 있는 DB 데이터 삭제
    videoRecDataDao.deleteVideoDb()

    val zipFile = File(MUFF_FOLDER, "evaluation.zip")
    val newUrl = if (CommonUtils.downloadUrl.endsWith("/")) {
      CommonUtils.downloadUrl + MUFF_CONST_MAPPING
    } else {
      CommonUtils.downloadUrl + "/" + MUFF_CONST_MAPPING
    }

    val response = MUFFervice.downloadCreate(percentUpdate).fileDownload(newUrl)
    response.suspendOnSuccess {
      data.byteStream().saveToFile(zipFile.absolutePath)
      emit(true)
    }.onError { map(ErrorResponseMapper) { onWarning(message) } }.onException { onError(message) }

    /* unZip(zipFile.absolutePath, ZIP_FOLDER.absolutePath) */
    emit(true)
  }.onStart { onStart() }.flowOn(Dispatchers.IO)

  private fun InputStream.saveToFile(savePath: String) = use { input ->
    File(savePath).outputStream().use { output ->
      input.copyTo(output)

      val rbc = Channels.newChannel(input)
      output.channel.transferFrom(rbc, 0, 1024.toLong())
      output.close()
      input.close()

      unZip(savePath, ZIP_FOLDER.absolutePath)
    }
  }

  private fun unZip(zipFilePath: String, targetPath: String) {
    ZipFile(zipFilePath).use { zip ->
      zip.entries().asSequence().forEach { entry ->
        zip.getInputStream(entry).use { input ->
          if (!entry.name.startsWith("evaluation") && (entry.name.endsWith(".sqlite") || entry.name.endsWith(".txt"))) {
            File(targetPath, entry.name).outputStream().use { output -> input.copyTo(output) }
          }
        }
      }
    }
  }

  @WorkerThread
  fun insertExtDatabase(stepStr: (String) -> Unit, onError: (String?) -> Unit) = flow {
    ZIP_FOLDER.listFiles()?.forEach {
      if (it.name.endsWith(".sqlite")) {
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
  }.catch { e -> /* onError(e.message) */ }.flowOn(Dispatchers.IO)


  private suspend fun prt2220Nt(helper: EpsDbHelper, outPrint: (String) -> Unit) {
    val hlperMap: MutableMap<Int, List<Long>> = mutableMapOf()

    val total = helper.tbEpOsw2220NtCount

    if (total > PARSER) {
      val divCount = total / PARSER
      val exmneList = helper.tbEpOsw2220NtExmneList

      for (i: Int in 1..divCount) {
        val jList: MutableList<Long> = mutableListOf()
        for (j: Int in ((i - 1) * PARSER) until (i * PARSER)) {
          jList.add(exmneList[j])
        }
        hlperMap[i - 1] = jList
      }
      val iList: MutableList<Long> = mutableListOf()
      for (i: Int in (divCount * PARSER) until total) {
        iList.add(exmneList[i])
      }
      hlperMap[divCount] = iList

      for (i: Int in 0 until hlperMap.keys.size) {
        outPrint("2220NT 분할DB 작업 $i")
        val list = helper.tbEpOsw2220NtBetween(hlperMap[i]!!.first(), hlperMap[i]!!.last())
        tbEpOsw2220ntdao.insertTbEpOsw2220Dao(list)
        delay(1000L)
      }
    } else {
      tbEpOsw2220ntdao.insertTbEpOsw2220Dao(helper.tbEpOsw2220Nt)
    }
  }

  @WorkerThread
  suspend  fun insertLog(kind: String, content: String, userId: String) {
    val logInfo = TB_EP_OSW_LOG(tbEpOswLogDao.getMaxLogId()+1, kind, content, userId, DateUtil.getNowDate(null))
    tbEpOswLogDao.insertLog(logInfo)
  }
}