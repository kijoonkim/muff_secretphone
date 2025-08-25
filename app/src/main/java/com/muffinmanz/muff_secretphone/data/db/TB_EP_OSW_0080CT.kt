package com.muffinmanz.muff_secretphone.data.db


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "TB_EP_OSW_0080CT")
data class TB_EP_OSW_0080CT(
  @PrimaryKey
  @Json(name = "DTS_IDCD")
  val dTSIDCD: String,
  @Json(name = "DTS_TYIN_EGNM")
  val dTSTYINEGNM: String,
  @Json(name = "DTS_TYIN_KRNM")
  val dTSTYINKRNM: String,
  @Json(name = "IDCD")
  val iDCD: String,
  @Json(name = "RGSR_ID")
  val rGSRID: String,
  @Json(name = "RGST_DT")
  val rGSTDT: String,
  @Json(name = "UPDT_DT")
  val uPDTDT: String,
  @Json(name = "UPPS_ID")
  val uPPSID: String,
  @Json(name = "USYN")
  val uSYN: String
)