package com.hlc.mywallet.feature.bonus

import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.lib_base.net.safeRequestWithoutData
import com.hlc.mywallet.data.api.MainService
import com.hlc.mywallet.data.model.req.ClaimBonusReq
import com.hlc.mywallet.data.model.resp.NewbieSummaryResp
import com.hlc.mywallet.data.model.resp.NewbieTaskResp
import com.hlc.mywallet.data.model.resp.ReferralResp
import javax.inject.Inject

/**
 * @author Wade
 * @since 2026/5/19
 */
class BonusRepository @Inject constructor(
    private val mainService: MainService
) {

    suspend fun getNewbieSummary(): ApiResult<NewbieSummaryResp> {
        return safeRequest {
            mainService.newbieSummary()
        }
    }

    suspend fun claimBonus(taskCode: String,targetId: String? = null): ApiResult<Unit> {
        return safeRequestWithoutData {
            mainService.claimBonus(ClaimBonusReq(taskCode = taskCode,targetId))
        }
    }

    suspend fun newbieList(): ApiResult<List<NewbieTaskResp>> {
        return safeRequest {
            mainService.newbieList()
        }
    }

    suspend fun referralList(): ApiResult<ReferralResp> {
        return safeRequest {
            mainService.referralList()
        }
    }
}
