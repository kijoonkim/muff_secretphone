package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface TB_EP_OSW_2230NTDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTbEpOsw2230Dao(list: List<TB_EP_OSW_2230NT>)
}