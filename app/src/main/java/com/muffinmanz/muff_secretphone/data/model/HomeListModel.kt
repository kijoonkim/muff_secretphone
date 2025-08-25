package com.muffinmanz.muff_secretphone.data.model

data class HomeListModel(
  val img: ByteArray?,
  val sectors: String,
  val detailSectors: String,
  val offNumber: String,
  val sex: String,
  val birth: String,
  val name: String,
  val iCheck: String?,
  val examNo: String,
  val skillNum: String
)