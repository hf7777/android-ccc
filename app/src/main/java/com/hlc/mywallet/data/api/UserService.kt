package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.req.LoginReq
import com.hlc.mywallet.data.model.req.RegisterReq
import com.hlc.mywallet.data.model.resp.BillsResp
import com.hlc.mywallet.data.model.resp.CaptchaImage
import com.hlc.mywallet.data.model.resp.CheckBindingResp
import com.hlc.mywallet.data.model.resp.DepositInrResp
import com.hlc.mywallet.data.model.resp.LoginResp
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.data.model.resp.OrderInrResp
import com.hlc.mywallet.data.model.resp.OrderUsdtResp
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import com.hlc.mywallet.data.model.resp.WithdrawRecordResp
import com.hlc.mywallet.data.model.resp.WithdrawStatus
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @GET("/app/captchaImage")
    suspend fun captchaImage(): BaseResponse<CaptchaImage>

    @POST("/app/login")
    suspend fun login(@Body request: LoginReq): BaseResponse<LoginResp>

    @GET("/app/mine/statistics")
    suspend fun getUserStatistics(): BaseResponse<UserStatisticsResp>

    @POST("/app/wallet/getCanPayWallets")
    suspend fun getMyWallet(): BaseResponse<List<MyWalletResp>>

    @POST("/app/deposit/inr/myList")
    suspend fun getOrderInrList(@Body requestBody: RequestBody): BaseResponse<OrderInrResp>

    @POST("/app/deposit/usdt/myList")
    suspend fun getOrderUsdtList(@Body requestBody: RequestBody): BaseResponse<OrderUsdtResp>

    @GET("/app/checkBinding")
    suspend fun checkBinding(): BaseResponse<CheckBindingResp>

    /**
     * 账单
     */
    @POST("/app/mine/bills")
    suspend fun getBills(@Body requestBody: RequestBody): BaseResponse<BillsResp>

    /**
     * 重置密码
     */
    @POST("/app/resetLoginPassword")
    suspend fun resetPassword(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 发送忘记密码otp
     */
    @POST("/app/sendForgetPasswordOtp")
    suspend fun sendPasswordOtp(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 注销
     */
    @POST("/app/logout")
    suspend fun logout(): BaseResponse<String>

    /**
     * 注册
     */
    @POST("/app/register")
    suspend fun register(@Body registerReq: RegisterReq): BaseResponse<String>

    /**
     * 查询提现状态
     */
    @GET("/app/withdraw/status")
    suspend fun getWithdrawStatus(): BaseResponse<WithdrawStatus>

    /**
     * 提交提现申请
     */
    @POST("/app/withdraw/submit")
    suspend fun submitWithdraw(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 查询提现记录
     */
    @POST("/app/withdraw/records")
    suspend fun withdrawRecords(@Body requestBody: RequestBody): BaseResponse<WithdrawRecordResp>

    /**
     * 开启自动代付
     */
    @POST("/app/withdraw/startAutoBuy")
    suspend fun startAutoBuy(): BaseResponse<String>

    /**
     * 关闭自动代付
     */
    @POST("/app/withdraw/stopAutoBuy")
    suspend fun stopAutoBuy(): BaseResponse<String>

}
