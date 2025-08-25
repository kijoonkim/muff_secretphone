package com.muffinmanz.muff_secretphone.data.db

import androidx.room.*

import com.muffinmanz.muff_secretphone.data.model.HomeListModel
import com.muffinmanz.muff_secretphone.data.model.VideoListModel

import kotlinx.coroutines.flow.Flow

@Dao
interface TB_EP_OSW_2220NTDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTbEpOsw2220Dao(list: List<TB_EP_OSW_2220NT>)

	@Query(
		"SELECT COUNT(*) " +
		"FROM " +
        "  TB_EP_OSW_2220NT A " +
        ", TB_EP_OSW_0070CT B " +
		", TB_EP_OSW_0080CT C " +
		", TB_EP_OSW_2230NT E " +
		"WHERE A.iDCD = B.iDCD " +
        "AND A.dTSIDCD = C.dTSIDCD " +
        "AND A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "AND E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe"
	)
	suspend fun fetchRcptCount(evlDe: String, amPmSecd: String): String?

	@Query(
		"SELECT COUNT(*) " +
        "FROM " +
        "  TB_EP_OSW_2220NT A" +
        ", TB_EP_OSW_0070CT B" +
        ", TB_EP_OSW_0080CT C" +
        ", TB_EP_OSW_2230NT E " +
        "WHERE A.iDCD = B.iDCD " +
        "AND A.dTSIDCD = C.dTSIDCD " +
        "AND A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "AND A.aYEXYN = 'Y' " +
        "AND E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe"
	)
	fun fetchAplyCount(evlDe: String, amPmSecd: String): Flow<String?>

	@Query(
		"SELECT COUNT(*) " +
        "FROM " +
        "  TB_EP_OSW_2220NT A " +
        ", TB_EP_OSW_0070CT B " +
        ", TB_EP_OSW_0080CT C " +
        ", TB_EP_OSW_2230NT E " +
        "WHERE A.iDCD = B.iDCD " +
        "AND A.dTSIDCD = C.dTSIDCD " +
        "AND A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "AND A.aYEXYN = 'N' " +
        "AND E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe"
	)
	fun fetchAbsentCount(evlDe: String, amPmSecd: String): Flow<String?>

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
        "ORDER BY A.tMPRAYEXNO"
	)
	fun fetchFlowInterViewerList(evlDe: String, amPmSecd: String): Flow<List<HomeListModel>>

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
        "AND (:sector IS NULL OR B.TYBN = :sector) " +
        "AND (:detailSector IS NULL OR C.dTSTYINKRNM = :detailSector) " +
        "ORDER BY A.tMPRAYEXNO"
	)
	suspend fun fetchInterViewerList(
		evlDe: String,
		amPmSecd: String,
		sector: String?,
		detailSector: String?
	): List<HomeListModel>?

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
        "AND ((A.tMPRAYEXNO LIKE '%' || :offNumber || '%') AND (A.PNM LIKE '%' || :name || '%') AND (A.bRDE LIKE '%' || :birth || '%')) " +
        "AND (:sector IS NULL OR B.TYBN = :sector) " +
        "AND (:detailSector IS NULL OR C.dTSTYINKRNM = :detailSector) " +
        "ORDER BY A.tMPRAYEXNO"
	)
	suspend fun fetchInterViewerList(
		evlDe: String,
		amPmSecd: String,
		offNumber: String?,
		name: String?,
		birth: String?,
		sector: String?,
		detailSector: String?
	): List<HomeListModel>?

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
        "AND ( " +
		"  (A.tMPRAYEXNO > :offNum) " +
		"  OR " +
		"  (A.tMPRAYEXNO = :offNum AND A.sKLLAYEXNO > :skillNum)" +
		") " +
		"ORDER BY A.tMPRAYEXNO, A.sKLLAYEXNO LIMIT 1"
	)
	suspend fun fetchNextUser(
        offNum: String,
		skillNum: String,
        evlDe: String,
        amPmSecd: String
    ): HomeListModel?

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
		"AND ( " +
		"  (A.tMPRAYEXNO < :offNum) " +
		"  OR " +
		"  (A.tMPRAYEXNO = :offNum AND A.sKLLAYEXNO < :skillNum)" +
		") " +
		"ORDER BY A.tMPRAYEXNO DESC, A.sKLLAYEXNO DESC LIMIT 1"
	)
	suspend fun fetchPrevUser(
        offNum: String,
		skillNum: String,
        evlDe: String,
        amPmSecd: String
    ): HomeListModel?

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        ", CASE WHEN COALESCE(V.useAt, '') = 'Y' THEN '완료' ELSE '대기' END AS vCheck " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "LEFT JOIN VideoRecData V ON A.kLNGEXAMAYEXNO = V.examNo " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
        "ORDER BY A.tMPRAYEXNO"
	)
	fun fetchFlowVideoList(
        evlDe: String,
        amPmSecd: String
    ): Flow<List<VideoListModel>>

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        ", CASE WHEN COALESCE(V.useAt, '') = 'Y' THEN '완료' ELSE '대기' END AS vCheck " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "LEFT JOIN VideoRecData V ON A.kLNGEXAMAYEXNO = V.examNo " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
        "ORDER BY V.useAt ASC, A.tMPRAYEXNO ASC"
	)
	fun fetchFlowVideoListSorted(
        evlDe: String,
        amPmSecd: String
    ): Flow<List<VideoListModel>>

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        ", CASE WHEN COALESCE(V.useAt, '') = 'Y' THEN '완료' ELSE '대기' END AS vCheck " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "LEFT JOIN VideoRecData V ON A.kLNGEXAMAYEXNO = V.examNo " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
        "ORDER BY A.tMPRAYEXNO"
	)
	suspend fun fetchVideoList(
        evlDe: String,
        amPmSecd: String
    ): List<VideoListModel>?

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        ", CASE WHEN COALESCE(V.useAt, '') = 'Y' THEN '완료' ELSE '대기' END AS vCheck " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "LEFT JOIN VideoRecData V ON A.kLNGEXAMAYEXNO = V.examNo " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
        "AND (:sector IS NULL OR B.TYBN = :sector) " +
        "AND (:detailSector IS NULL OR C.dTSTYINKRNM = :detailSector) " +
        "AND ((:vCheck IS NULL) OR (:vCheck = '완료' AND V.useAt = 'Y') OR (:vCheck = '대기' AND COALESCE(V.useAt, '') != 'Y')) " +
        "ORDER BY A.tMPRAYEXNO"
	)
	suspend fun fetchVideoList(
		evlDe: String,
		amPmSecd: String,
		sector: String?,
		detailSector: String?,
		vCheck: String?
	): List<VideoListModel>?

	@Query(
		"SELECT " +
        "  A.iMGEFLNM AS img " +
        ", B.TYBN AS sectors " +
        ", C.dTSTYINKRNM AS detailSectors " +
        ", A.tMPRAYEXNO AS offNumber " +
        ", A.sXDSCD AS sex " +
        ", substr(A.bRDE, 1, 10) AS birth " +
        ", A.PNM AS name " +
        ", A.aYEXYN AS iCheck " +
        ", A.kLNGEXAMAYEXNO AS examNo " +
        ", A.sKLLAYEXNO AS skillNum " +
        ", CASE WHEN COALESCE(V.useAt, '') = 'Y' THEN '완료' ELSE '대기' END AS vCheck " +
        "FROM TB_EP_OSW_2220NT A " +
        "JOIN TB_EP_OSW_0070CT B ON A.iDCD = B.iDCD " +
        "JOIN TB_EP_OSW_0080CT C ON A.dTSIDCD = C.dTSIDCD " +
        "JOIN TB_EP_OSW_2230NT E ON A.tYINASIGSEQ = E.tYINASIGSEQ " +
        "LEFT JOIN VideoRecData V ON A.kLNGEXAMAYEXNO = V.examNo " +
        "WHERE E.aMPMSECD = :amPmSecd " +
        "AND E.eVLDE = :evlDe " +
        "AND (A.tMPRAYEXNO  LIKE '%' || :offNumber || '%' OR A.PNM LIKE '%' || :name || '%') " +
        "AND (:sector IS NULL OR B.TYBN = :sector) " +
        "AND (:detailSector IS NULL OR C.dTSTYINKRNM = :detailSector) " +
        "AND ((:vCheck IS NULL) OR (:vCheck = '완료' AND V.useAt = 'Y') OR (:vCheck = '대기' AND COALESCE(V.useAt, '') != 'Y')) " +
        "ORDER BY A.tMPRAYEXNO"
	)
	suspend fun fetchVideoList(
		evlDe: String,
		amPmSecd: String,
		offNumber: String?,
		name: String?,
		sector: String?,
		detailSector: String?,
		vCheck: String?
	): List<VideoListModel>?

	@Query(
        "SELECT COUNT(aYEXYN) " +
        "FROM tb_ep_osw_2220nt " +
        "WHERE aYEXYN = 'Y'"
    )
	suspend fun aYeXynCount(): Int

	@Query(
		"SELECT tYBN " +
        "FROM TB_EP_OSW_0070CT " +
        "WHERE iDCD = ( " +
        "  SELECT iDCD " +
        "  FROM TB_EP_OSW_0080CT " +
        "  WHERE dTSTYINKRNM = :selectedDetailSector " +
        ") " +
        "ORDER BY iDCD"
	)
	suspend fun fetchSectorsList(selectedDetailSector: String): List<String>

	@Query(
        "SELECT tYBN " +
        "FROM TB_EP_OSW_0070CT " +
        "ORDER BY iDCD"
    )
	suspend fun fetchAllSectorsList(): List<String>

	@Query(
		"SELECT dTSTYINKRNM " +
        "FROM TB_EP_OSW_0080CT " +
        "WHERE iDCD = ( " +
        "  SELECT iDCD " +
        "  FROM TB_EP_OSW_0070CT " +
        "  WHERE TYBN = :selectedSector " +
        ") " +
        "ORDER BY dTSIDCD"
	)
	suspend fun fetchDetailSectorsList(selectedSector: String): List<String>

	@Query(
        "SELECT dTSTYINKRNM " +
        "FROM TB_EP_OSW_0080CT " +
        "ORDER BY dTSIDCD"
    )
	suspend fun fetchAllDetailSectorsList(): List<String>

	@Query(
        "UPDATE TB_EP_OSW_2220NT " +
        "SET aYEXYN = :status " +
        "WHERE kLNGEXAMAYEXNO = :examNo"
    )
	suspend fun updateICheck(
        status: String,
        examNo: String
    )

	@Transaction
	suspend fun updateICheckAndNext(
		status: String,
		examNo: String,
		offNum: String,
		skillNum: String,
		evlDe: String,
		amPmSecd: String
	): HomeListModel? {
		updateICheck(status, examNo)
		return fetchNextUser(offNum, skillNum, evlDe, amPmSecd)
	}

	@Query(
        "UPDATE TB_EP_OSW_2220NT " +
        "SET sXDSCD = :sex " +
        "WHERE kLNGEXAMAYEXNO = :examNo"
    )
	suspend fun updateGender(
        sex: String,
        examNo: String
    )

	@Transaction
	suspend fun updateUserGender(
		sex: String,
		examNo: String,
		offNum: String,
		skillNum: String,
		evlDe: String,
		amPmSecd: String
	): HomeListModel? {
		updateGender(sex, examNo)
		return fetchNextUser(offNum, skillNum, evlDe, amPmSecd)
	}
}
