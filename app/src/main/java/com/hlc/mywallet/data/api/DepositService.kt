package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.resp.UsdtPayResp
import com.hlc.mywallet.data.model.resp.DepositInrResp
import com.hlc.mywallet.data.model.resp.InrDetailResp
import com.hlc.mywallet.data.model.resp.TeamStatisticsResp
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DepositService {

    /**
     * 本土货币充值列表(抢单广场)
     */
    @POST("/app/deposit/inr/list")
    suspend fun depositInrList(@Body requestBody: RequestBody): BaseResponse<DepositInrResp>

    /**
     * USDT充值拉单
     */
    @POST("/app/deposit/usdt/pay")
    suspend fun usdtPay(@Body requestBody: RequestBody): BaseResponse<UsdtPayResp>

    /**
     * 本土货币充值订单抢单
     */
    @POST("/app/deposit/inr/grab")
    suspend fun depositGrab(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 本土货币充值订单详情
     */
    @POST("/app/deposit/inr/detail")
    suspend fun inrDetail(@Body requestBody: RequestBody): BaseResponse<InrDetailResp>
}
