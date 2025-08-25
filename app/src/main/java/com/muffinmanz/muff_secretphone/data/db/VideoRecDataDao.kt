package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.muffinmanz.muff_secretphone.data.model.HomeListModel

@Dao
interface VideoRecDataDao {

  @Query("SELECT * FROM VideoRecData WHERE examNo = :examNo")
  suspend fun fetchVideoInfo(examNo: String): VideoRecData?

  @Query("SELECT COUNT(*) CNT FROM VideoRecData WHERE examNo = :examNo")
  suspend fun fetchVideoInfoCnt(examNo: String): Int

  @Query("SELECT COUNT(*) CNT FROM VideoRecData WHERE examNo = :examNo AND isEnglish = 1")
  suspend fun fetchVideoInfoEngCnt(examNo: String): Int

  @Query("SELECT COUNT(*) CNT FROM VideoRecData WHERE examNo = :examNo AND isEnglish = 0")
  suspend fun fetchVideoInfoKorCnt(examNo: String): Int

  @Query("SELECT * FROM VideoRecData WHERE examNo = :examNo AND useAt =:vChk")
  suspend fun fetchVideoInfo(examNo: String, vChk: String?): VideoRecData?

  @Query("SELECT * FROM VideoRecData")
  suspend fun fetchVideoRecList(): List<VideoRecData>

  @Query("UPDATE VideoRecData SET useAt = 'N'")
  suspend fun notUseVideo()

  @Query("DELETE FROM TB_EP_OSW_0070CT")
  suspend fun delete0070()

  @Query("DELETE FROM TB_EP_OSW_0080CT")
  suspend fun delete0080()

  @Query("DELETE FROM TB_EP_OSW_2110NT")
  suspend fun delete2110()

  @Query("DELETE FROM TB_EP_OSW_2220NT")
  suspend fun delete2220()

  @Query("DELETE FROM TB_EP_OSW_2230NT")
  suspend fun delete2230()

  @Query("DELETE FROM VideoRecData")
  suspend fun videoRecData()

  @Transaction
  suspend fun deleteVideoDb() {
    delete0070()
    delete0080()
    delete2110()
    delete2220()
    delete2230()
    videoRecData()
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVideoRec(video: VideoRecData)
}