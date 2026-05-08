package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.UsdtPayResp
import com.hlc.mywallet.data.model.resp.DepositInrResp
import com.hlc.mywallet.data.model.resp.TeamStatisticsResp
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DepositService {

    @POST("/app/deposit/inr/list")
    suspend fun depositInrList(@Body requestBody: RequestBody): BaseResponse<DepositInrResp>

    @POST("/app/deposit/usdt/pay")
    suspend fun usdtPay(@Body requestBody: RequestBody): BaseResponse<UsdtPayResp>
}
