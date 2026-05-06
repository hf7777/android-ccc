package com.hlc.mywallet.feature.tutorial

import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.mywallet.data.api.HomeService
import com.hlc.mywallet.data.model.resp.TutorialResp
import javax.inject.Inject

class TutorialRepository @Inject constructor(
    private val homeService: HomeService
) {
    suspend fun getTutorials(): ApiResult<List<TutorialResp>> {
        return safeRequest {
            homeService.tutorials()
        }
    }
}
