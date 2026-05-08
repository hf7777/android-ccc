package com.hlc.mywallet.feature.home

import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.mywallet.storage.CacheKeys
import com.hlc.mywallet.storage.CacheStorage
import com.hlc.mywallet.data.api.HomeService
import com.hlc.mywallet.data.model.resp.BannersResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import com.hlc.mywallet.data.model.resp.TutorialResp
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val homeService: HomeService,
    private val cacheStorage: CacheStorage
) {
    suspend fun getBanners(): ApiResult<List<BannersResp>> {
        return safeRequest {
            homeService.banners()
        }
    }

    suspend fun getPriceInfo(): ApiResult<PriceInfoResp> {
        val result = safeRequest {
            homeService.priceInfo()
        }
        if (result is ApiResult.Success) {
            cacheStorage.save(CacheKeys.PRICE_INFO, result.data)
        }
        return result
    }

    suspend fun getTutorials(): ApiResult<List<TutorialResp>> {
        return safeRequest {
            homeService.tutorials()
        }
    }
}
