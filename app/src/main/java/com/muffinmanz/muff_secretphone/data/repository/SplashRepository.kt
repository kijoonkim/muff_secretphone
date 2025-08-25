package com.muffinmanz.muff_secretphone.data.repository

import androidx.annotation.WorkerThread
import com.muffinmanz.muff_secretphone.data.db.IP_CONFIGDao
import com.muffinmanz.muff_secretphone.utilities.CommonUtils
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SplashRepository @Inject constructor(private val ipConfigDao: IP_CONFIGDao){

  @WorkerThread
  fun fetchIpConfig() = flow {
    val ipConfig = ipConfigDao.fetchIpConfig()

    if(!ipConfig.isNullOrEmpty()) {
      if(ipConfig.size == 1) {
        if(ipConfig[0].url.isNotEmpty()) {
          CommonUtils.downloadUrl = ipConfig[0].url
        }
      } else {
        if(ipConfig[0].url.isNotEmpty()) {
          CommonUtils.downloadUrl = ipConfig[0].url
        }
        if(ipConfig[1].url.isNotEmpty()) {
          CommonUtils.uploadUrl = ipConfig[1].url
        }
      }
    }

    emit(true)
  }
}