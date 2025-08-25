package com.muffinmanz.muff_secretphone.network

import okhttp3.ResponseBody
import okio.Buffer
import okio.ForwardingSource
import okio.Source
import okio.buffer

class ProgressResponseBody (val responseBody: ResponseBody, val onAttachmentDownloadUpdate: (Int) -> Unit) : ResponseBody() {

  private var bufferedSource = source(responseBody.source()).buffer()

  override fun contentLength() = responseBody.contentLength()

  override fun contentType() = responseBody.contentType()

  override fun source() = bufferedSource

  private fun source(source: Source) : Source {
    return object : ForwardingSource(source) {
      var totalBytesRead = 0L

      override fun read(sink: Buffer, byteCount: Long): Long {
        val bytesRead = super.read(sink, byteCount)
        totalBytesRead += if (bytesRead != -1L) bytesRead else 0
        val percent = if (bytesRead == -1L) 100f else totalBytesRead.toFloat() / responseBody.contentLength().toFloat() * 100
        onAttachmentDownloadUpdate(percent.toInt())
        return bytesRead
      }
    }
  }
}