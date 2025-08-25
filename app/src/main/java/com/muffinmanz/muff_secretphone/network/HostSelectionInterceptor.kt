package com.muffinmanz.muff_secretphone.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HostSelectionInterceptor : Interceptor {
  @Volatile
  private var host: String? = null
  fun setHost(host: String?) {
    this.host = host
  }

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    var request: Request = chain.request()
    val host = host
    if (host != null) {
      val newUrl = host.toHttpUrlOrNull()
      request = request.newBuilder()
        .url(newUrl ?: chain.request().url)
        .build()
    }
    return chain.proceed(request)
  }
}