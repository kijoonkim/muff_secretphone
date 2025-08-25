package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TB_EP_OSW_LOGDao {

  @Query("SELECT MAX(logId) FROM TB_EP_OSW_LOG")
  suspend fun getMaxLogId() : Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(logInfo: TB_EP_OSW_LOG)

  @Query(
    "SELECT logId, kind, content, userId, regDt FROM TB_EP_OSW_LOG WHERE KIND = :kind ORDER BY regDt DESC LIMIT 1 "
  )
  suspend fun fetchLogInfo(kind: String): TB_EP_OSW_LOG?
}