package com.muffinmanz.muff_secretphone.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "VideoRecData", primaryKeys = ["examNo", "isEnglish", "isKorea"])
@JsonClass(generateAdapter = true)
data class VideoRecData(
  val examNo: String,
  val videoPath: String,
  val saveDate: String,
  val isEnglish: Boolean = false,
  val isKorea: Boolean = false,
  val useAt: String = "Y"
)
