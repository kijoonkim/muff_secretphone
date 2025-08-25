package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface TB_EP_OSW_0070CTDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTbEpOsw0070Dao(list: List<TB_EP_OSW_0070CT>)

}