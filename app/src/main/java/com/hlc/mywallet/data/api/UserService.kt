package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.req.LoginReq
import com.hlc.mywallet.data.model.resp.CaptchaImage
import com.hlc.mywallet.data.model.resp.LoginResp
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {

    @GET("get")
    suspend fun fetchUser(): BaseResponse<ApiPayload>

    @GET("/app/captchaImage")
    suspend fun captchaImage(): BaseResponse<CaptchaImage>

    @POST("/app/login")
    suspend fun login(@Body request: LoginReq): BaseResponse<LoginResp>
}
