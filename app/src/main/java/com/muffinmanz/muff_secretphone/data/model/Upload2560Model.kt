package com.muffinmanz.muff_secretphone.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Upload2560Model(
  @Json(name = "SPNT_SEQ")
  val sPNTSEQ: Int,
  @Json(name = "KLNG_EXAM_AYEX_NO")
  val kLNGEXAMAYEXNO: String,
  @Json(name = "SPNT_CTGRY")
  val sPNTCTGRY: String,
  @Json(name = "SPNT_CN")
  val sPNTCN: String,
  @Json(name = "RGST_DT")
  val rGSTDT: String,
  @Json(name = "RGSR_ID")
  val rGSRID: String,
  @Json(name = "UPDT_DT")
  val uPDTDT: String,
  @Json(name = "UPPS_ID")
  val uPPSID: String,
  @Json(name = "GRDR_SEQ")
  val gRDRSEQ: String
)