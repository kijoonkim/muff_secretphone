package com.muffinmanz.muff_secretphone.data.db


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "TB_EP_OSW_2230NT")
@JsonClass(generateAdapter = true)
data class TB_EP_OSW_2230NT(
  @Json(name = "AM_PM_SECD")
  val aMPMSECD: String,
  @Json(name = "DCSN_YN")
  val dCSNYN: String,
  @Json(name = "DTS_IDCD")
  val dTSIDCD: String,
  @Json(name = "EVL_DE")
  val eVLDE: String,
  @Json(name = "EVPL_ASIG_SEQ")
  val eVPLASIGSEQ: Int,
  @Json(name = "EXMI_ASIG_SEQ")
  val eXMIASIGSEQ: Int,
  @Json(name = "GRUP_NO")
  val gRUPNO: String,
  @Json(name = "OPRN_PLAN_SEQ")
  val oPRNPLANSEQ: Int,
  @Json(name = "RGSR_ID")
  val rGSRID: String,
  @Json(name = "RGST_DT")
  val rGSTDT: String,
  @Json(name = "TYIN_ASIG_NMPR")
  val tYINASIGNMPR: Int,
  @PrimaryKey
  @Json(name = "TYIN_ASIG_SEQ")
  val tYINASIGSEQ: Int,
  @Json(name = "UPDT_DT")
  val uPDTDT: String,
  @Json(name = "UPPS_ID")
  val uPPSID: String,
  @Json(name = "USYN")
  val uSYN: String
)