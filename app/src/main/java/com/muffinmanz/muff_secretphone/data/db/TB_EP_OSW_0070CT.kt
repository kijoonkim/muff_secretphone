package com.muffinmanz.muff_secretphone.data.db


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "TB_EP_OSW_0070CT")
data class TB_EP_OSW_0070CT(
  @PrimaryKey
  @Json(name = "IDCD")
  val iDCD: String,
  @Json(name = "RGSR_ID")
  val rGSRID: String,
  @Json(name = "RGST_DT")
  val rGSTDT: String,
  @Json(name = "TYBN")
  val tYBN: String,
  @Json(name = "UPDT_DT")
  val uPDTDT: String,
  @Json(name = "UPPS_ID")
  val uPPSID: String,
  @Json(name = "USYN")
  val uSYN: String
)