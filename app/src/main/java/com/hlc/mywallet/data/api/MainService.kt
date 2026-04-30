package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import retrofit2.http.GET

interface MainService {
    @GET("get")
    suspend fun fetchMain(): BaseResponse<ApiPayload>
}
