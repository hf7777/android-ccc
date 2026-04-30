package com.hlc.mywallet.feature.home

import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.mywallet.data.api.HomeService
import com.hlc.mywallet.data.model.resp.BannersResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val homeService: HomeService
) {
    suspend fun getBanners(): ApiResult<List<BannersResp>> {
        return safeRequest {
            homeService.banners()
        }
    }

    suspend fun getPriceInfo(): ApiResult<PriceInfoResp> {
        return safeRequest {
            homeService.priceInfo()
        }
    }
}
