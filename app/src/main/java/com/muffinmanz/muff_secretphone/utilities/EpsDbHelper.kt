package com.muffinmanz.muff_secretphone.utilities

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.database.getStringOrNull
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_0070CT
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_0080CT
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2110NT
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2220NT
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2230NT
import com.muffinmanz.muff_secretphone.data.db.TB_EP_OSW_2560NT

class EpsDbHelper(path: String) {

  private val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)

  val tbEpOsw2110Nt
    get() = run {
      val tb2110List: MutableList<TB_EP_OSW_2110NT> = mutableListOf()
      db.rawQuery("SELECT * FROM TB_EP_OSW_2110NT", null).use {
          while (it.moveToNext()) {


            tb2110List.add(
              TB_EP_OSW_2110NT(
                bGDE = it.getString(it.getColumnIndexOrThrow("BGDE")),
                dTYPAPRRCEPTBGDE = it.getString(it.getColumnIndexOrThrow("DTY_PAPR_RCEPT_BGDE")),
                dTYPAPRRCEPTENDE = it.getString(it.getColumnIndexOrThrow("DTY_PAPR_RCEPT_ENDE")),
                eNDE = it.getString(it.getColumnIndexOrThrow("ENDE")),
                nTNCD = it.getString(it.getColumnIndexOrThrow("NTN_CD")),
                oPRNPLANIDNFID = it.getString(it.getColumnIndexOrThrow("OPRN_PLAN_IDNF_ID")),
                oPRNPLANSEQ = it.getInt(it.getColumnIndexOrThrow("OPRN_PLAN_SEQ")),
                oPRNSTCD = it.getString(it.getColumnIndexOrThrow("OPRN_STCD")),
                oPRNTME = it.getInt(it.getColumnIndexOrThrow("OPRN_TME")),
                oPRNTMEDTS = it.getInt(it.getColumnIndexOrThrow("OPRN_TME_DTS")),
                oPRNYEAR = it.getString(it.getColumnIndexOrThrow("OPRN_YEAR")),
                rGSRID = it.getString(it.getColumnIndexOrThrow("RGSR_ID")),
                rGSTDT = it.getString(it.getColumnIndexOrThrow("RGST_DT")),
                sSCNPRDT = it.getString(it.getColumnIndexOrThrow("SSCN_PRDT")),
                uPDTDT = it.getString(it.getColumnIndexOrThrow("UPDT_DT")),
                uPPSID = it.getString(it.getColumnIndexOrThrow("UPPS_ID")),
                uSYN = it.getString(it.getColumnIndexOrThrow("USYN"))
              )
            )
          }
      }
      tb2110List
    }

  val tbEpOsw2220NtCount
    get() = run {
      var count: Int
      db.rawQuery("SELECT COUNT(*) FROM TB_EP_OSW_2220NT", null).use {
        it.moveToFirst()
        count = it.getInt(0)
      }
      count
  }

  val tbEpOsw2220NtExmneList
    get() = run {
      val exmneNoList:MutableList<Long> = mutableListOf()

      db.rawQuery("SELECT \"EXMI_NO\" FROM TB_EP_OSW_2220NT order by EXMI_NO", null).use {
        while (it.moveToNext()) {
          exmneNoList.add(it.getLong(it.getColumnIndexOrThrow("EXMI_NO")))
        }
      }
      exmneNoList
  }

  fun tbEpOsw2220NtBetween(first:Long, last:Long): List<TB_EP_OSW_2220NT> {
    val exmneList: MutableList<TB_EP_OSW_2220NT> = mutableListOf()
    db.rawQuery("SELECT * FROM TB_EP_OSW_2220NT WHERE EXMI_NO BETWEEN $first AND $last ORDER BY EXMI_NO", null).use {
      while (it.moveToNext()) {
        val klngExamAyexNo = it.getString(it.getColumnIndexOrThrow("KLNG_EXAM_AYEX_NO"));

        exmneList.add(
          TB_EP_OSW_2220NT(
            aYEXYN = it.getStringOrNull(it.getColumnIndexOrThrow("AYEX_YN")),
            bRDE = it.getString(it.getColumnIndexOrThrow("BRDE")),
            dTSIDCD = it.getString(it.getColumnIndexOrThrow("DTS_IDCD")),
            eVPLASIGSEQ = it.getInt(it.getColumnIndexOrThrow("EVPL_ASIG_SEQ")),
            eXMIASIGCFRMYN = it.getString(it.getColumnIndexOrThrow("EXMI_ASIG_CFRM_YN")),
            eXMIASIGSEQ = it.getInt(it.getColumnIndexOrThrow("EXMI_ASIG_SEQ")),
            eXMINO = it.getLong(it.getColumnIndexOrThrow("EXMI_NO")),
            gRUPNO = it.getString(it.getColumnIndexOrThrow("GRUP_NO")),
            iDCD = it.getString(it.getColumnIndexOrThrow("IDCD")),
            iMGEFLNM = it.getBlob(it.getColumnIndexOrThrow("IMGE_FLNM")),
            kLNGEXAMAYEXNO = klngExamAyexNo,
            kLNGEXAMCD = it.getString(it.getColumnIndexOrThrow("KLNG_EXAM_CD")),
            nTNCD = it.getString(it.getColumnIndexOrThrow("NTN_CD")),
            oPRNPLANSEQ = it.getInt(it.getColumnIndexOrThrow("OPRN_PLAN_SEQ")),
            oPRNTME = it.getInt(it.getColumnIndexOrThrow("OPRN_TME")),
            oPRNYEAR = it.getString(it.getColumnIndexOrThrow("OPRN_YEAR")),
            pNM = it.getString(it.getColumnIndexOrThrow("PNM")),
            rGSRID = it.getString(it.getColumnIndexOrThrow("RGSR_ID")),
            rGSTDT = it.getString(it.getColumnIndexOrThrow("RGST_DT")),
            sKLLAYEXNO = it.getString(it.getColumnIndexOrThrow("SKLL_AYEX_NO")),
            sXDSCD = it.getString(it.getColumnIndexOrThrow("SXDS_CD")),
            tMPRAYEXNO = it.getString(it.getColumnIndexOrThrow("TMPR_AYEX_NO")),
            tYINASIGSEQ = it.getInt(it.getColumnIndexOrThrow("TYIN_ASIG_SEQ")),
            uPDTDT = it.getString(it.getColumnIndexOrThrow("UPDT_DT")),
            uPPSID = it.getString(it.getColumnIndexOrThrow("UPPS_ID")),
            uSYN = it.getString(it.getColumnIndexOrThrow("USYN")),
            zIP = it.getString(it.getColumnIndexOrThrow("ZIP"))
          )
        )
      }
    }
    return exmneList
  }

  val tbEpOsw2220Nt
    get() = run {
      val tb2220List: MutableList<TB_EP_OSW_2220NT> = mutableListOf()
      db.rawQuery("SELECT * FROM TB_EP_OSW_2220NT", null).use {
          while (it.moveToNext()) {
            val klngExamAyexNo = it.getString(it.getColumnIndexOrThrow("KLNG_EXAM_AYEX_NO"));

            tb2220List.add(
              TB_EP_OSW_2220NT(
                aYEXYN = it.getStringOrNull(it.getColumnIndexOrThrow("AYEX_YN")),
                bRDE = it.getString(it.getColumnIndexOrThrow("BRDE")),
                dTSIDCD = it.getString(it.getColumnIndexOrThrow("DTS_IDCD")),
                eVPLASIGSEQ = it.getInt(it.getColumnIndexOrThrow("EVPL_ASIG_SEQ")),
                eXMIASIGCFRMYN = it.getString(it.getColumnIndexOrThrow("EXMI_ASIG_CFRM_YN")),
                eXMIASIGSEQ = it.getInt(it.getColumnIndexOrThrow("EXMI_ASIG_SEQ")),
                eXMINO = it.getLong(it.getColumnIndexOrThrow("EXMI_NO")),
                gRUPNO = it.getString(it.getColumnIndexOrThrow("GRUP_NO")),
                iDCD = it.getString(it.getColumnIndexOrThrow("IDCD")),
                iMGEFLNM = it.getBlob(it.getColumnIndexOrThrow("IMGE_FLNM")),
                kLNGEXAMAYEXNO = klngExamAyexNo,
                kLNGEXAMCD = it.getString(it.getColumnIndexOrThrow("KLNG_EXAM_CD")),
                nTNCD = it.getString(it.getColumnIndexOrThrow("NTN_CD")),
                oPRNPLANSEQ = it.getInt(it.getColumnIndexOrThrow("OPRN_PLAN_SEQ")),
                oPRNTME = it.getInt(it.getColumnIndexOrThrow("OPRN_TME")),
                oPRNYEAR = it.getString(it.getColumnIndexOrThrow("OPRN_YEAR")),
                pNM = it.getString(it.getColumnIndexOrThrow("PNM")),
                rGSRID = it.getString(it.getColumnIndexOrThrow("RGSR_ID")),
                rGSTDT = it.getString(it.getColumnIndexOrThrow("RGST_DT")),
                sKLLAYEXNO = it.getString(it.getColumnIndexOrThrow("SKLL_AYEX_NO")),
                sXDSCD = it.getString(it.getColumnIndexOrThrow("SXDS_CD")),
                tMPRAYEXNO = it.getString(it.getColumnIndexOrThrow("TMPR_AYEX_NO")),
                tYINASIGSEQ = it.getInt(it.getColumnIndexOrThrow("TYIN_ASIG_SEQ")),
                uPDTDT = it.getString(it.getColumnIndexOrThrow("UPDT_DT")),
                uPPSID = it.getString(it.getColumnIndexOrThrow("UPPS_ID")),
                uSYN = it.getString(it.getColumnIndexOrThrow("USYN")),
                zIP = it.getString(it.getColumnIndexOrThrow("ZIP"))
              )
            )
          }
        }
      tb2220List
    }
  
  val tbEpOsw0070Ct
    get() = run {
      val tb0070List: MutableList<TB_EP_OSW_0070CT> = mutableListOf()
      db.rawQuery("SELECT * FROM TB_EP_OSW_0070CT", null).use {
          while (it.moveToNext()) {
            tb0070List.add(
              TB_EP_OSW_0070CT(
                iDCD = it.getString(it.getColumnIndexOrThrow("IDCD")),
                rGSRID = it.getString(it.getColumnIndexOrThrow("RGSR_ID")),
                rGSTDT = it.getString(it.getColumnIndexOrThrow("RGST_DT")),
                tYBN = it.getString(it.getColumnIndexOrThrow("TYBN")),
                uPDTDT = it.getString(it.getColumnIndexOrThrow("UPDT_DT")),
                uPPSID = it.getString(it.getColumnIndexOrThrow("UPPS_ID")),
                uSYN = it.getString(it.getColumnIndexOrThrow("USYN"))
              )
            )
          }
      }
      tb0070List
    }

  val tbEpOsw0080Ct
    get() = run {
      val tb0080List: MutableList<TB_EP_OSW_0080CT> = mutableListOf()
      db.rawQuery("SELECT * FROM TB_EP_OSW_0080CT", null).use {
          while (it.moveToNext()) {
            tb0080List.add(
              TB_EP_OSW_0080CT(
                dTSIDCD = it.getString(it.getColumnIndexOrThrow("DTS_IDCD")),
                dTSTYINEGNM = it.getString(it.getColumnIndexOrThrow("DTS_TYIN_EGNM")),
                dTSTYINKRNM = it.getString(it.getColumnIndexOrThrow("DTS_TYIN_KRNM")),
                iDCD = it.getString(it.getColumnIndexOrThrow("IDCD")),
                rGSRID = it.getString(it.getColumnIndexOrThrow("RGSR_ID")),
                rGSTDT = it.getString(it.getColumnIndexOrThrow("RGST_DT")),
                uPDTDT = it.getString(it.getColumnIndexOrThrow("UPDT_DT")),
                uPPSID = it.getString(it.getColumnIndexOrThrow("UPPS_ID")),
                uSYN = it.getString(it.getColumnIndexOrThrow("USYN"))
              )
            )
          }
      }
      tb0080List
    }

  val tbEpOsw2230Nt
  get() = run {
    val tb2230List: MutableList<TB_EP_OSW_2230NT> = mutableListOf()
    db.rawQuery("SELECT * FROM TB_EP_OSW_2230NT", null).use {
      while (it.moveToNext()) {
        tb2230List.add(
          TB_EP_OSW_2230NT(
            aMPMSECD = it.getString(it.getColumnIndexOrThrow("AM_PM_SECD")),
            dCSNYN = it.getString(it.getColumnIndexOrThrow("DCSN_YN")),
            dTSIDCD = it.getString(it.getColumnIndexOrThrow("DTS_IDCD")),
            eVLDE = it.getString(it.getColumnIndexOrThrow("EVL_DE")),
            eVPLASIGSEQ = it.getInt(it.getColumnIndexOrThrow("EVPL_ASIG_SEQ")),
            eXMIASIGSEQ = it.getInt(it.getColumnIndexOrThrow("EXMI_ASIG_SEQ")),
            gRUPNO = it.getString(it.getColumnIndexOrThrow("GRUP_NO")),
            oPRNPLANSEQ = it.getInt(it.getColumnIndexOrThrow("OPRN_PLAN_SEQ")),
            rGSRID = it.getString(it.getColumnIndexOrThrow("RGSR_ID")),
            rGSTDT = it.getString(it.getColumnIndexOrThrow("RGST_DT")),
            tYINASIGNMPR = it.getInt(it.getColumnIndexOrThrow("TYIN_ASIG_NMPR")),
            tYINASIGSEQ = it.getInt(it.getColumnIndexOrThrow("TYIN_ASIG_SEQ")),
            uPDTDT = it.getString(it.getColumnIndexOrThrow("UPDT_DT")),
            uPPSID = it.getString(it.getColumnIndexOrThrow("UPPS_ID")),
            uSYN = it.getString(it.getColumnIndexOrThrow("USYN"))
          )
        )
      }
    }
    tb2230List
  }

  val tbEpOsw2560Nt
    get() = run {
      val tb2560List: MutableList<TB_EP_OSW_2560NT> = mutableListOf()
      db.rawQuery("SELECT * FROM TB_EP_OSW_2560NT", null).use {
        while (it.moveToNext()) {
          tb2560List.add(
            TB_EP_OSW_2560NT(
              sPNTSEQ = it.getInt(it.getColumnIndexOrThrow("SPNT_SEQ")),
              kLNGEXAMAYEXNO = it.getString(it.getColumnIndexOrThrow("KLNG_EXAM_AYEX_NO")),
              sPNTCTGRY = it.getString(it.getColumnIndexOrThrow("SPNT_CTGRY")),
              sPNTCN = it.getString(it.getColumnIndexOrThrow("SPNT_CN")),
              rGSTDT = it.getString(it.getColumnIndexOrThrow("RGST_DT")),
              rGSRID = it.getString(it.getColumnIndexOrThrow("RGSR_ID")),
              uPDTDT = it.getString(it.getColumnIndexOrThrow("UPDT_DT")),
              uPPSID = it.getString(it.getColumnIndexOrThrow("UPPS_ID")),
              gRDRSEQ = it.getString(it.getColumnIndexOrThrow("GRDR_SEQ"))
            )
          )
        }
      }
      tb2560List
    }
}