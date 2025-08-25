package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LOGIN_LOGDao {

  @Query("SELECT MAX(SEQ) FROM LOGIN_LOG")
  suspend fun getMaxSeq() : Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLoginLog(loginLog: LOGIN_LOG)
}