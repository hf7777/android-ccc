package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.ApiPayload
import com.hlc.mywallet.data.model.resp.TeamStatisticsResp
import retrofit2.http.GET

interface TeamService {

    @GET("/app/team/statistics")
    suspend fun teamStatistics(): BaseResponse<TeamStatisticsResp>
}
