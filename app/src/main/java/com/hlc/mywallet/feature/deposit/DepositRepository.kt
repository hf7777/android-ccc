package com.hlc.mywallet.feature.deposit

import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.lib_base.net.safeRequestWithoutData
import com.hlc.mywallet.data.api.DepositService
import com.hlc.mywallet.data.api.UserService
import com.hlc.mywallet.data.model.resp.UsdtPayResp
import com.hlc.mywallet.data.model.resp.DepositInrResp
import com.hlc.mywallet.data.model.resp.InrDetailResp
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import com.hlc.mywallet.storage.CacheKeys
import com.hlc.mywallet.storage.CacheStorage
import javax.inject.Inject

class DepositRepository @Inject constructor(
    private val depositService: DepositService,
    private val cacheStorage: CacheStorage
) {

    suspend fun getDepositInrList(
        page: Int,
        pageSize: Int,
        minAmount: String? = null,
        maxAmount: String? = null,
        orderAmountSort: String? = null
    ): ApiResult<DepositInrResp> {
        return safeRequest {
            val params = mutableListOf<Pair<String, Any?>>(
                "pageNum" to page,
                "pageSize" to pageSize
            )
            minAmount?.let { params.add("minAmount" to it) }
            maxAmount?.let { params.add("maxAmount" to it) }
            orderAmountSort?.let { params.add("orderAmountSort" to it) }

            depositService.depositInrList(
                buildJsonBody(*params.toTypedArray())
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

    suspend fun depositInrGrab(platformOrderNo: String, walletId: String): ApiResult<String> {
        return safeRequest {
            depositService.depositGrab(
                buildJsonBody(
                    "platformOrderNo" to platformOrderNo,
                    "walletId" to walletId
                )
            )
        }
    }

    suspend fun inrDetail(grabRecordId: String): ApiResult<InrDetailResp> {
        return safeRequest {
            depositService.inrDetail(
                buildJsonBody(
                    "grabRecordId" to grabRecordId
                )
            )
        }
    }

    suspend fun cancelInrOrder(grabRecordId: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            depositService.cancelInrOrder(
                buildJsonBody(
                    "grabRecordId" to grabRecordId
                )
            )
        }
    }

    suspend fun inrDepositConfirm(platformOrderNo: String, grabId: String, utr: String, voucherUrl: String): ApiResult<Unit> {
        return  safeRequestWithoutData {
            depositService.inrDepositConfirm(
                buildJsonBody(
                    "platformOrderNo" to platformOrderNo,
                    "grabId" to grabId,
                    "utr" to utr,
                    "voucherUrl" to voucherUrl
                )
            )
        }
    }
}
