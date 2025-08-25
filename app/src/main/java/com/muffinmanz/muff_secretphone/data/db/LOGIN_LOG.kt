package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "LOGIN_LOG")
data class LOGIN_LOG(
  @PrimaryKey
  val seq: Int,
  val user: String,
  val desc: String?,
  val rgstDt: String
)
