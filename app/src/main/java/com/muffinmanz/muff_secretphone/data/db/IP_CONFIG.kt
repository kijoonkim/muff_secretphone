package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "IP_CONFIG")
data class IP_CONFIG(
  @PrimaryKey
  val ipType: Int,  // 0: 다운로드, 1: 업로드
  val url: String,
  val desc: String?
)
