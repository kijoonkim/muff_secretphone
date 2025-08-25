package com.muffinmanz.muff_secretphone.network

import com.muffinmanz.muff_secretphone.MUFF_API_URL
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MUFFervice {

  @Streaming
  @GET
  suspend fun fileDownload(@Url downloadUrl: String, @Query("file_name") fileName: String = "evaluation.zip"): ApiResponse<ResponseBody>

  @Streaming
  @GET
  suspend fun apkDownload(@Url downloadUrl: String, @Query("file_name") fileName: String = "MUFF_Interview.apk"): ApiResponse<ResponseBody>

  @Multipart
  @POST
  suspend fun videoFileUpload(@Url uploadUrl: String, @Part file: List<MultipartBody.Part>): ApiResponse<ResponseBody>

  @Multipart
  @POST
  suspend fun jsonFileUpload(@Url uploadUrl: String, @Part file: MultipartBody.Part): ApiResponse<ResponseBody>


  companion object {
    private val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)

    fun downloadCreate(onAttachmentDownloadUpdate: (Int) -> Unit): MUFFervice {
      return Retrofit.Builder()
        .baseUrl(MUFF_API_URL)
        .client(createOkHttpProgressClient(onAttachmentDownloadUpdate))
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
        .build()
        .create(MUFFervice::class.java)
    }

    private fun createOkHttpProgressClient(onAttachmentDownloadUpdate: (Int) -> Unit): OkHttpClient {
      val builder = OkHttpClient.Builder()
      builder.apply {
        retryOnConnectionFailure(true)
        addInterceptor(httpLoggingInterceptor)
        connectTimeout(20, TimeUnit.SECONDS)
        readTimeout(20, TimeUnit.SECONDS)
        writeTimeout(20, TimeUnit.SECONDS)
        addInterceptor { chain ->
          val originalResponse = chain.proceed(chain.request())
          originalResponse.newBuilder()
            .body(originalResponse.body?.let {
              ProgressResponseBody(it, onAttachmentDownloadUpdate)
            })
            .build()
        }
      }
      return builder.build()
    }
  }
}