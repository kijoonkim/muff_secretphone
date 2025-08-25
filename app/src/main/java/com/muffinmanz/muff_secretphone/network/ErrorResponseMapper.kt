package com.muffinmanz.muff_secretphone.network

import com.skydoves.sandwich.ApiErrorModelMapper
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.message
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ErrorResponseMapper : ApiErrorModelMapper<ErrorResponseMapper.SimpleErrorResponse> {

  private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): SimpleErrorResponse {
    val jsonAdapter = moshi.adapter(CustomErrorBody::class.java)
    val code = apiErrorResponse.statusCode.code
    val returnMessage = apiErrorResponse.errorBody!!.source()

    return try {
      SimpleErrorResponse(code, jsonAdapter.fromJson(returnMessage)?.message)
    } catch (e: Exception) {
      SimpleErrorResponse(code, apiErrorResponse.message())
    }
  }

  @JsonClass(generateAdapter = true)
  data class SimpleErrorResponse(
    val conde: Int,
    val message: String?
  )

  @JsonClass(generateAdapter = true)
  data class CustomErrorBody(
    val message: String
  )
}