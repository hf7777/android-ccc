package com.hlc.lib_net

import com.hlc.lib_base.BaseResponse
import retrofit2.http.GET

interface MainService {
    @GET("get")
    suspend fun fetchMain(): BaseResponse<ApiPayload>
}
