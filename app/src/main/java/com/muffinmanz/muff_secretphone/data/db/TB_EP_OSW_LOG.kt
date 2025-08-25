package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "TB_EP_OSW_LOG")
data class TB_EP_OSW_LOG(
  @PrimaryKey
  @Json(name = "LOG_ID")
  val logId: Int,
  @Json(name = "KIND")
  val kind: String,
  @Json(name = "CONTENT")
  val content: String?,
  @Json(name = "USER_ID")
  val userId: String?,
  @Json(name = "REG_DT")
  val regDt: String
)
