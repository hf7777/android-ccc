package com.hlc.lib_base.net

import android.content.Context
import com.blankj.utilcode.util.StringUtils
import com.hlc.lib_base.R
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
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
    fun convertException(e: Exception): ApiException {
        return when (e) {
            is ApiException -> e
            is JsonDataException, is JsonEncodingException -> {
                ApiException.ParseException(StringUtils.getString(R.string.error_parse))
            }
            is IOException -> {
                ApiException.NetworkException(StringUtils.getString(R.string.error_network))
            }
            is HttpException -> {
                when (e.code()) {
                    500, 502, 503 -> ApiException.ServerException(StringUtils.getString(R.string.error_server))
                    404 -> ApiException.NetworkException(StringUtils.getString(R.string.error_api_not_found))
                    else -> ApiException.UnknownException(
                        e.message ?: StringUtils.getString(R.string.error_unknown)
                    )
                }
            }
            else -> ApiException.UnknownException(
                e.message ?: StringUtils.getString(R.string.error_unknown)
            )
        }
    }
}

sealed class ApiResult<out T> {
    object Idle : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val exception: ApiException) : ApiResult<Nothing>()
}
