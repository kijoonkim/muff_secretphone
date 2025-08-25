package com.muffinmanz.muff_secretphone.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadModel(
  @Json(name = "birth")
  val birth: String,
  @Json(name = "birthId")
  val birthId: String,
  @Json(name = "businessName")
  val businessName: String,
  @Json(name = "detailFieldName")
  val detailFieldName: String,
  @Json(name = "dtm")
  val dtm: String,
  @Json(name = "evaluatorsName")
  val evaluatorsName: String,
  @Json(name = "examTestNumber")
  val examTestNumber: String,
  @Json(name = "gender")
  val gender: String,
  @Json(name = "genderChanged")
  val genderChanged: Boolean,
  @Json(name = "iCheck")
  val iCheck: String,
  @Json(name = "id")
  val id: Int,
  @Json(name = "imageOffCheck")
  val imageOffCheck: Boolean,
  @Json(name = "imageUrl")
  val imageUrl: String,
  @Json(name = "login_id")
  val loginId: String,
  @Json(name = "name")
  val name: String,
  @Json(name = "number")
  val number: String,
  @Json(name = "vCheck")
  val vCheck: String,
  @Json(name = "videoUrl")
  val videoUrl: String
)