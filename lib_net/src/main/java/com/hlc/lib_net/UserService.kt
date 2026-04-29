package com.hlc.lib_net

import com.hlc.lib_base.BaseResponse
import retrofit2.http.GET

interface UserService {
    @GET("get")
    suspend fun fetchUser(): BaseResponse<ApiPayload>
}
