package com.hlc.lib_base.net

import android.content.Context
import com.blankj.utilcode.util.StringUtils
import com.hjq.toast.Toaster
import com.hlc.lib_base.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

suspend fun <T> BaseResponse<T>.handleRequest(context: Context): T {
    return withContext(Dispatchers.IO) {
        if (isSuccess) {
            data ?: throw ApiException.ParseException(context.getString(R.string.error_data_empty))
        } else {
            throw ApiException.BusinessException(code, msg)
        }
    }
}

fun <T> requestFlow(context: Context, block: suspend () -> BaseResponse<T>): Flow<T> {
    return flow {
        val response = block()
        if (response.isSuccess) {
            response.data?.let { emit(it) } 
                ?: throw ApiException.ParseException(context.getString(R.string.error_data_empty))
        } else {
            throw ApiException.BusinessException(response.code, response.msg)
        }
    }.flowOn(Dispatchers.IO)
}

suspend fun <T> safeRequest(block: suspend () -> BaseResponse<T>): ApiResult<T> {
    return try {
        val response = block()
        if (response.isSuccess) {
            response.data?.let { ApiResult.Success(it) } 
                ?: ApiResult.Error(ApiException.ParseException(StringUtils.getString(R.string.error_data_empty)))
        } else {
            ApiResult.Error(ApiException.BusinessException(response.code, response.msg))
        }
    } catch (e: Exception) {
        ApiResult.Error(ExceptionHandler.convertException(e))
    }
}
