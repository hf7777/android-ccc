package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.resp.BannersResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import retrofit2.http.GET

interface HomeService {

    @GET("/app/home/banners")
    suspend fun banners(): BaseResponse<List<BannersResp>>

    @GET("/app/home/priceInfo")
    suspend fun priceInfo(): BaseResponse<PriceInfoResp>
}
