package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.resp.BannersResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import com.hlc.mywallet.data.model.resp.TutorialResp
import retrofit2.http.GET

interface HomeService {

    /**
     * 获取轮播图列表
     */
    @GET("/app/home/banners")
    suspend fun banners(): BaseResponse<List<BannersResp>>

    /**
     * 获取首页价格信息
     */
    @GET("/app/home/priceInfo")
    suspend fun priceInfo(): BaseResponse<PriceInfoResp>

    /**
     * 获取教程列表
     */
    @GET("/app/home/tutorials")
    suspend fun tutorials(): BaseResponse<List<TutorialResp>>
}
