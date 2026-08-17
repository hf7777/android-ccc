package com.hlc.mywallet.di

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * 根据请求头动态设置单次请求超时时间
 * 通过 Retrofit @Headers("X-Request-Timeout: 90") 标记长耗时接口
 * 该 Header 不会发送到后端
 */
class TimeoutInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val timeoutSeconds = request.header(HEADER_TIMEOUT)?.toLongOrNull()

        val newChain = if (timeoutSeconds != null) {
            chain.withConnectTimeout(timeoutSeconds.toInt(), TimeUnit.SECONDS)
                .withReadTimeout(timeoutSeconds.toInt(), TimeUnit.SECONDS)
                .withWriteTimeout(timeoutSeconds.toInt(), TimeUnit.SECONDS)
        } else {
            chain
        }

        val newRequest = request.newBuilder()
            .removeHeader(HEADER_TIMEOUT)
            .build()

        return newChain.proceed(newRequest)
    }

    companion object {
        const val HEADER_TIMEOUT = "X-Request-Timeout"
    }
}
