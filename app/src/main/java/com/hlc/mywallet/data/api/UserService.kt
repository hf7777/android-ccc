package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.req.LoginReq
import com.hlc.mywallet.data.model.resp.BillsResp
import com.hlc.mywallet.data.model.resp.CaptchaImage
import com.hlc.mywallet.data.model.resp.CheckBindingResp
import com.hlc.mywallet.data.model.resp.DepositInrResp
import com.hlc.mywallet.data.model.resp.LoginResp
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.data.model.resp.OrderInrResp
import com.hlc.mywallet.data.model.resp.OrderUsdtResp
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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
}
