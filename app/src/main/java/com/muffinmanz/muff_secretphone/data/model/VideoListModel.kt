package com.muffinmanz.muff_secretphone.data.model

data class VideoListModel(
  val img: ByteArray?,
  val sectors: String,
  var isEnglish: Boolean?,
  var isKorea: Boolean?,
  var hasEnglishVideo: Boolean?,
  var hasKoreanVideo: Boolean?,
  val detailSectors: String,
  val offNumber: String,
  val sex: String,
  val birth: String,
  val name: String,
  val iCheck: String?,
  var vCheck: String?,
  val examNo: String,
  val skillNum: String,
  var videoPath: String?
)