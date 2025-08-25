package com.muffinmanz.muff_secretphone

import android.os.Environment
import android.os.Environment.DIRECTORY_DCIM
import android.os.Environment.DIRECTORY_DOWNLOADS
import java.io.File

const val DATABASE_NAME = "MUFF_Header.db"
const val DATABASE_VERSION = 1

/**
 * 2220NT 테이블 몇명으로 끊어서 파싱할지 정하는 값
 */
const val PARSER = 1000
const val MUFF_API_URL = "http://192.168.0.7:8080/"
const val MUFF_CONST_MAPPING = "app/appDataDownload.do"
const val MUFF_CONST_APK = "app/apkDownload.do"
const val MUFF_CONST_UPLOAD = "app/uploadResult.do"

val VIDEO_FOLDER: File = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM), "MUFF")
val APK_FOLDER: File = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
var MUFF_FOLDER: File = File(Environment.getExternalStorageDirectory(), "MUFF")
var ZIP_FOLDER: File = File(MUFF_FOLDER, "zipData")

/**
 * 로그 종류
 */
const val LOG_KIND_001 = "LOGIN"
const val LOG_KIND_002 = "LOGOUT"
const val LOG_KIND_003 = "UPDATE"
const val LOG_KIND_004 = "UPLOAD"

