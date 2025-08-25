package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IP_CONFIGDao {

  @Query("SELECT * FROM IP_CONFIG")
  suspend fun fetchIpConfig(): List<IP_CONFIG>?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertIpConfig(ipConfig: IP_CONFIG)
}