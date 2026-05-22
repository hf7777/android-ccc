package com.hlc.mywallet.feature.team

import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.mywallet.data.api.TeamService
import com.hlc.mywallet.data.model.resp.SublineResp
import com.hlc.mywallet.data.model.resp.TeamStatisticsResp
import javax.inject.Inject

class TeamRepository @Inject constructor(
    private val teamService: TeamService
) {
    suspend fun getTeamStatistics(): ApiResult<TeamStatisticsResp> {
        return safeRequest {
            teamService.teamStatistics()
        }
    }

    suspend fun sublineList(level: String): ApiResult<List<SublineResp>> {
        return safeRequest {
            teamService.sublineList(
                buildJsonBody(
                    "level" to level
                )
            )
        }
    }
}
