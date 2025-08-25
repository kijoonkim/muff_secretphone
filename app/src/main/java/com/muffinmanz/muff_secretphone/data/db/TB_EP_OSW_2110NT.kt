package com.muffinmanz.muff_secretphone.data.db


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "TB_EP_OSW_2110NT")
@JsonClass(generateAdapter = true)
data class TB_EP_OSW_2110NT(
  @Json(name = "BGDE")
  val bGDE: String,
  @Json(name = "DTY_PAPR_RCEPT_BGDE")
  val dTYPAPRRCEPTBGDE: String,
  @Json(name = "DTY_PAPR_RCEPT_ENDE")
  val dTYPAPRRCEPTENDE: String,
  @Json(name = "ENDE")
  val eNDE: String,
  @Json(name = "NTN_CD")
  val nTNCD: String,
  @Json(name = "OPRN_PLAN_IDNF_ID")
  val oPRNPLANIDNFID: String,
  @PrimaryKey
  @Json(name = "OPRN_PLAN_SEQ")
  val oPRNPLANSEQ: Int,
  @Json(name = "OPRN_STCD")
  val oPRNSTCD: String,
  @Json(name = "OPRN_TME")
  val oPRNTME: Int,
  @Json(name = "OPRN_TME_DTS")
  val oPRNTMEDTS: Int,
  @Json(name = "OPRN_YEAR")
  val oPRNYEAR: String,
  @Json(name = "RGSR_ID")
  val rGSRID: String,
  @Json(name = "RGST_DT")
  val rGSTDT: String,
  @Json(name = "SSCN_PRDT")
  val sSCNPRDT: String,
  @Json(name = "UPDT_DT")
  val uPDTDT: String,
  @Json(name = "UPPS_ID")
  val uPPSID: String,
  @Json(name = "USYN")
  val uSYN: String
)