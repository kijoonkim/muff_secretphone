package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TB_EP_OSW_2110NTDao {

  @Query("SELECT * FROM TB_EP_OSW_2110NT LIMIT 1")
  suspend fun fetchTbEpOsw2110NtDataCheck() : TB_EP_OSW_2110NT?

  @Query("SELECT * FROM TB_EP_OSW_2110NT LIMIT 1")
  suspend fun fetchTbEpOsw2110Nt() : TB_EP_OSW_2110NT

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTbEpOsw2110Dao(list: List<TB_EP_OSW_2110NT>)
}