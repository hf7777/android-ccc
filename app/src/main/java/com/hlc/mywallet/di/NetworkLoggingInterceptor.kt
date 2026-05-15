package com.hlc.mywallet.di

import android.util.Log
import com.hlc.mywallet.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okio.Buffer
import java.io.EOFException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class NetworkLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!BuildConfig.DEBUG) {
            return chain.proceed(request)
        }

        val requestBodyString = request.body?.let { body ->
            if (body.isDuplex() || body.isOneShot()) {
                "[request body omitted]"
            } else {
                readRequestBody(body.contentType(), body)
            }
        } ?: ""

        val startNs = System.nanoTime()

        return try {
            val response = chain.proceed(request)
            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            val responseBodyString = response.peekBody(MAX_LOGGABLE_RESPONSE_BODY).string()

            logLongMessage(
                TAG,
                buildString {
                    appendLine("=== HTTP ${request.method} ${response.code} (${tookMs}ms) ===")
                    appendLine("URL: ${request.url}")
                    if (requestBodyString.isNotEmpty()) {
                        appendLine("Request Body: $requestBodyString")
                    }
                    appendLine("Response Body: $responseBodyString")
                    append("=== END HTTP ===")
                }
            )
            response
        } catch (e: Exception) {
            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            logLongMessage(
                TAG,
                buildString {
                    appendLine("=== HTTP ${request.method} FAILED (${tookMs}ms) ===")
                    appendLine("URL: ${request.url}")
                    if (requestBodyString.isNotEmpty()) {
                        appendLine("Request Body: $requestBodyString")
                    }
                    appendLine("Error: ${e.message}")
                    append("=== END HTTP ===")
                }
            )
            throw e
        }
    }

    private fun readRequestBody(contentType: MediaType?, body: okhttp3.RequestBody): String {
        return try {
            val buffer = Buffer()
            body.writeTo(buffer)
            if (!buffer.isPlaintext()) {
                "[binary ${body.contentLength()}-byte body omitted]"
            } else {
                val charset = contentType?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
                buffer.readString(charset)
            }
        } catch (_: Exception) {
            "[request body omitted]"
        }
    }

    private fun Buffer.isPlaintext(): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (size < 64L) size else 64L
            copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (_: EOFException) {
            false
        }
    }

    private fun logLongMessage(tag: String, message: String) {
        if (message.length <= LOG_CHUNK_SIZE) {
            Log.d(tag, message)
            return
        }
        var startIndex = 0
        while (startIndex < message.length) {
            val endIndex = minOf(startIndex + LOG_CHUNK_SIZE, message.length)
            Log.d(tag, message.substring(startIndex, endIndex))
            startIndex = endIndex
        }
    }

    companion object {
        private const val TAG = "OkHttp"
        private const val LOG_CHUNK_SIZE = 2000
        private const val MAX_LOGGABLE_RESPONSE_BODY = 1024 * 1024L
    }
}
