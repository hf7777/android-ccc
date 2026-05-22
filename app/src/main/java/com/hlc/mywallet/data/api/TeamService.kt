package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.resp.SublineResp
import com.hlc.mywallet.data.model.resp.TeamStatisticsResp
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TeamService {

    @GET("/app/team/statistics")
    suspend fun teamStatistics(): BaseResponse<TeamStatisticsResp>

    /**
     * 下级列表
     */
    @POST("/app/team/subordinate/list")
    suspend fun sublineList(@Body requestBody: RequestBody): BaseResponse<List<SublineResp>>
}
