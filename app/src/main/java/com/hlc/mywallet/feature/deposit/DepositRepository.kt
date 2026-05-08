package com.hlc.mywallet.feature.deposit

import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.mywallet.data.api.DepositService
import com.hlc.mywallet.data.model.UsdtPayResp
import com.hlc.mywallet.data.model.resp.DepositInrResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import com.hlc.mywallet.storage.CacheKeys
import com.hlc.mywallet.storage.CacheStorage
import javax.inject.Inject

class DepositRepository @Inject constructor(
    private val depositService: DepositService,
    private val cacheStorage: CacheStorage
) {

    suspend fun getDepositInrList(page: Int, pageSize: Int): ApiResult<DepositInrResp> {
        return safeRequest {
            depositService.depositInrList(
                buildJsonBody(
                    "pageNum" to page,
                    "pageSize" to pageSize
                )
            )
        }
    }

    suspend fun getCachedPriceInfo(): PriceInfoResp? {
        return cacheStorage.get(CacheKeys.PRICE_INFO, PriceInfoResp::class.java)
    }

    suspend fun usdtPay(usdtAmount: Int): ApiResult<UsdtPayResp> {
        return safeRequest {
            depositService.usdtPay(
                buildJsonBody(
                    "usdtAmount" to usdtAmount
                )
            )
        }
    }
}
