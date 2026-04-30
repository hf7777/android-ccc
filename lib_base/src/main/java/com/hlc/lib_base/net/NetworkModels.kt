package com.hlc.lib_base

import android.content.Context
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import java.io.IOException

data class BaseResponse<T>(
    val code: Int,
    val msg: String,
    val data: T?
) {
    val isSuccess: Boolean
        get() = code == 200
}

sealed class ApiException(message: String, val code: Int) : Exception(message) {
    class NetworkException(message: String) : ApiException(message, -1)
    class BusinessException(code: Int, message: String) : ApiException(message, code)
    class ParseException(message: String) : ApiException(message, -2)
    class ServerException(message: String) : ApiException(message, 500)
    class UnknownException(message: String) : ApiException(message, -99)
}

object ExceptionHandler {
    fun convertException(e: Exception, context: Context): ApiException {
        return when (e) {
            is ApiException -> e
            is JsonDataException, is JsonEncodingException -> {
                ApiException.ParseException(context.getString(R.string.error_parse))
            }
            is IOException -> {
                ApiException.NetworkException(context.getString(R.string.error_network))
            }
            is retrofit2.HttpException -> {
                when (e.code()) {
                    500, 502, 503 -> ApiException.ServerException(context.getString(R.string.error_server))
                    404 -> ApiException.NetworkException(context.getString(R.string.error_api_not_found))
                    else -> ApiException.UnknownException(
                        e.message ?: context.getString(R.string.error_unknown)
                    )
                }
            }
            else -> ApiException.UnknownException(
                e.message ?: context.getString(R.string.error_unknown)
            )
        }
    }
}

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val exception: ApiException) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}
