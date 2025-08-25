package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.muffinmanz.muff_secretphone.DATABASE_VERSION

@Database(
  entities = [IP_CONFIG::class, LOGIN_LOG::class, TB_EP_OSW_LOG::class, TB_EP_OSW_0070CT::class, TB_EP_OSW_0080CT::class, TB_EP_OSW_2110NT::class, TB_EP_OSW_2220NT::class, TB_EP_OSW_2230NT::class, TB_EP_OSW_2560NT::class, VideoRecData::class],
  version = DATABASE_VERSION,
  exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun ipConfigDao(): IP_CONFIGDao
  abstract fun loginLogDao(): LOGIN_LOGDao
  abstract fun tbEpOswLogDao(): TB_EP_OSW_LOGDao
  abstract fun tbEpOsw0070CtDao(): TB_EP_OSW_0070CTDao
  abstract fun tbEpOsw0080CtDao(): TB_EP_OSW_0080CTDao
  abstract fun tbEpOsw2110NtDao(): TB_EP_OSW_2110NTDao
  abstract fun tbEpOsw2220NtDao(): TB_EP_OSW_2220NTDao
  abstract fun tbEpOsw2230NtDao(): TB_EP_OSW_2230NTDao
  abstract fun tbEpOsw2560NtDao(): TB_EP_OSW_2560NTDao
  abstract fun videoRecDataDao(): VideoRecDataDao
}