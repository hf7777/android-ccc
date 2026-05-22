package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.req.ClaimBonusReq
import com.hlc.mywallet.data.model.resp.BalanceType
import com.hlc.mywallet.data.model.resp.BindCodeResp
import com.hlc.mywallet.data.model.resp.BulletinResp
import com.hlc.mywallet.data.model.resp.CustomerServiceResp
import com.hlc.mywallet.data.model.resp.NewbieSummaryResp
import com.hlc.mywallet.data.model.resp.NewbieTaskResp
import com.hlc.mywallet.data.model.resp.ReferralResp
import com.hlc.mywallet.data.model.resp.SliderCaptchaResp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    /**
     * 滑块验证码
     */
    @POST("/app/sliderCaptcha")
    suspend fun sliderCaptcha(): BaseResponse<SliderCaptchaResp>

    /**
     * 图片上传
     */
    @Multipart
    @POST("/app/deposit/uploadImg")
    suspend fun uploadImage(@Part file: MultipartBody.Part): BaseResponse<String>

    /**
     * 获取新手任务概览（是否完成 + 总奖励）
     */
    @GET("/app/bonus/newbie/summary")
    suspend fun newbieSummary(): BaseResponse<NewbieSummaryResp>

    /**
     * 领取奖励
     */
    @POST("/app/bonus/claim")
    suspend fun claimBonus(@Body claimBonus: ClaimBonusReq): BaseResponse<String>

    /**
     * 获取新手指引任务列表
     */
    @GET("/app/bonus/newbie")
    suspend fun newbieList(): BaseResponse<List<NewbieTaskResp>>

    /**
     * 获取新手指引任务列表
     */
    @GET("/app/bonus/referral")
    suspend fun referralList(): BaseResponse<ReferralResp>

    /**
     * 获取新手指引任务列表
     */
    @POST("/app/announcement/latest")
    suspend fun bulletinList(): BaseResponse<List<BulletinResp>>

    /**
     * 确认公告
     */
    @POST("/app/announcement/confirm")
    suspend fun confirmBulletin(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 客服
     */
    @GET("/app/mine/customer/services")
    suspend fun customerService(): BaseResponse<List<CustomerServiceResp>>

}
