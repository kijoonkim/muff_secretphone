package com.muffinmanz.muff_secretphone.di

import com.muffinmanz.muff_secretphone.MUFF_API_URL
import com.muffinmanz.muff_secretphone.network.MUFFClient
import com.muffinmanz.muff_secretphone.network.MUFFervice
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .connectTimeout(20, TimeUnit.SECONDS)
      .readTimeout(20, TimeUnit.SECONDS)
      .writeTimeout(20, TimeUnit.SECONDS)
      .retryOnConnectionFailure(true)
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(MUFF_API_URL)
      .addConverterFactory(MoshiConverterFactory.create())
      .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
      .build()
  }

  @Provides
  @Singleton
  fun provideEpsApiService(retrofit: Retrofit): MUFFervice {
    return retrofit.create(MUFFervice::class.java)
  }

  @Provides
  @Singleton
  fun provideEpsClient(epService: MUFFervice): MUFFClient {
    return MUFFClient(epService)
  }
}