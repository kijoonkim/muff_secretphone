package com.muffinmanz.muff_secretphone.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MUFFClient @Inject constructor(private val epService: MUFFervice) {

  suspend fun videoFileUpload(uploadUrl: String, file: List<MultipartBody.Part>) = epService.videoFileUpload(uploadUrl, file)

  suspend fun jsonFileUpload(uploadUrl: String, jsonFile: MultipartBody.Part) = epService.jsonFileUpload(uploadUrl, jsonFile)

}