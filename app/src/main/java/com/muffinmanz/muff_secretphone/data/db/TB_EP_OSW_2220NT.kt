package com.muffinmanz.muff_secretphone.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "TB_EP_OSW_2220NT")
@JsonClass(generateAdapter = true)
data class TB_EP_OSW_2220NT(
  @Json(name = "AYEX_YN")
  val aYEXYN: String?,
  @Json(name = "BRDE")
  val bRDE: String,
  @Json(name = "DTS_IDCD")
  val dTSIDCD: String,
  @Json(name = "EVPL_ASIG_SEQ")
  val eVPLASIGSEQ: Int,
  @Json(name = "EXMI_ASIG_CFRM_YN")
  val eXMIASIGCFRMYN: String,
  @Json(name = "EXMI_ASIG_SEQ")
  val eXMIASIGSEQ: Int,
  @PrimaryKey
  @Json(name = "EXMI_NO")
  val eXMINO: Long,
  @Json(name = "GRUP_NO")
  val gRUPNO: String,
  @Json(name = "IDCD")
  val iDCD: String,
  @Json(name = "IMGE_FLNM")
  @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
  val iMGEFLNM: ByteArray?,
  @Json(name = "KLNG_EXAM_AYEX_NO")
  val kLNGEXAMAYEXNO: String,
  @Json(name = "KLNG_EXAM_CD")
  val kLNGEXAMCD: String,
  @Json(name = "NTN_CD")
  val nTNCD: String,
  @Json(name = "OPRN_PLAN_SEQ")
  val oPRNPLANSEQ: Int,
  @Json(name = "OPRN_TME")
  val oPRNTME: Int,
  @Json(name = "OPRN_YEAR")
  val oPRNYEAR: String,
  @Json(name = "PNM")
  val pNM: String,
  @Json(name = "RGSR_ID")
  val rGSRID: String,
  @Json(name = "RGST_DT")
  val rGSTDT: String,
  @Json(name = "SKLL_AYEX_NO")
  val sKLLAYEXNO: String,
  @Json(name = "SXDS_CD")
  val sXDSCD: String,
  @Json(name = "TMPR_AYEX_NO")
  val tMPRAYEXNO: String,
  @Json(name = "TYIN_ASIG_SEQ")
  val tYINASIGSEQ: Int,
  @Json(name = "UPDT_DT")
  val uPDTDT: String,
  @Json(name = "UPPS_ID")
  val uPPSID: String,
  @Json(name = "USYN")
  val uSYN: String,
  @Json(name = "ZIP")
  val zIP: String
)