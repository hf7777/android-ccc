package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.resp.BalanceType
import com.hlc.mywallet.data.model.resp.BindCodeResp
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MainService {

    /**
     * 获取tg绑定码
     */
    @GET("/app/tg/bindCode")
    suspend fun getBindCode(): BaseResponse<BindCodeResp>

    /**
     * 发送tg验证码
     */
    @POST("/app/tg/sendPinOtp")
    suspend fun sendPinOtp(): BaseResponse<String>

     /**
     * 设置pin
     */
    @POST("/app/pin/reset")
    suspend fun setPin(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 验证pin
     */
    @POST("/app/pin/check")
    suspend fun checkPin(@Body requestBody: RequestBody): BaseResponse<Boolean>

    /**
     * 发送OTP
     */
    @POST("app/wallet/sendOtp")
    suspend fun sendTgOtp(@Body requestBody: RequestBody): BaseResponse<Boolean>

    /**
     * 账单类型
     */
    @POST("/app/select/antBalanceType")
    suspend fun antBalanceType(): BaseResponse<List<BalanceType>>

}
