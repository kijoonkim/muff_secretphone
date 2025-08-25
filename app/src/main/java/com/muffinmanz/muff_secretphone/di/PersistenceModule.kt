package com.muffinmanz.muff_secretphone.di

import android.app.Application
import androidx.room.PrimaryKey
import androidx.room.Room
import com.muffinmanz.muff_secretphone.DATABASE_NAME
import com.muffinmanz.muff_secretphone.data.db.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

  @Provides
  @Singleton
  fun provideMoshi(): Moshi {
    return Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .build()
  }

  @Provides
  @Singleton
  fun provideAppDatabase(
    application: Application
  ): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, DATABASE_NAME).build()
  }

  @Provides
  @Singleton
  fun provideIpConfigDao(appDatabase: AppDatabase): IP_CONFIGDao {
    return appDatabase.ipConfigDao()
  }

  @Provides
  @Singleton
  fun provideLoginLogDao(appDatabase: AppDatabase): LOGIN_LOGDao {
    return appDatabase.loginLogDao()
  }

  @Provides
  @Singleton
  fun provideTbEpOsw0070CtDao(appDatabase: AppDatabase): TB_EP_OSW_0070CTDao {
    return appDatabase.tbEpOsw0070CtDao()
  }

  @Provides
  @Singleton
  fun provideTbEpOsw0080CtDao(appDatabase: AppDatabase): TB_EP_OSW_0080CTDao {
    return appDatabase.tbEpOsw0080CtDao()
  }

  @Provides
  @Singleton
  fun provideTbEpOsw2110NtDao(appDatabase: AppDatabase): TB_EP_OSW_2110NTDao {
    return appDatabase.tbEpOsw2110NtDao()
  }

  @Provides
  @Singleton
  fun provideTbEpOsw2220NtDao(appDatabase: AppDatabase): TB_EP_OSW_2220NTDao {
    return appDatabase.tbEpOsw2220NtDao()
  }

  @Provides
  @Singleton
  fun provideTbEpOsw2230NtDao(appDatabase: AppDatabase): TB_EP_OSW_2230NTDao {
    return appDatabase.tbEpOsw2230NtDao()
  }

  @Provides
  @Singleton
  fun provideTbEpOsw2560NtDao(appDatabase: AppDatabase): TB_EP_OSW_2560NTDao {
    return appDatabase.tbEpOsw2560NtDao()
  }

  @Provides
  @Singleton
  fun provideVideoRecDataDao(appDatabase: AppDatabase): VideoRecDataDao {
    return appDatabase.videoRecDataDao()
  }

  @Provides
  @Singleton
  fun provideTbEpOswLogDao(appDatabase: AppDatabase): TB_EP_OSW_LOGDao {
    return appDatabase.tbEpOswLogDao()
  }
}