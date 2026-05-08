package com.hlc.mywallet.feature.mine

import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.mywallet.data.api.UserService
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
) {
    suspend fun getUserStatistics(): ApiResult<UserStatisticsResp> {
        return safeRequest {
            userService.getUserStatistics()
        }
    }
}
