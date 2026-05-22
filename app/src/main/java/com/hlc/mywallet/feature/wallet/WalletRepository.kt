package com.hlc.mywallet.feature.wallet

import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.lib_base.net.safeRequestWithoutData
import com.hlc.mywallet.data.api.WalletService
import com.hlc.mywallet.data.model.resp.ApplyPermissionResp
import com.hlc.mywallet.data.model.resp.PayChannelResp
import com.hlc.mywallet.data.model.resp.WalletListResp
import okhttp3.RequestBody
import javax.inject.Inject

class WalletRepository @Inject constructor(
    private val walletService: WalletService
) {

    suspend fun getWalletList(params: RequestBody): ApiResult<WalletListResp> {
        return safeRequest {
            walletService.getWalletList(params)
        }
    }

    suspend fun startSelling(phone: String, channelCode: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.startSelling(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode
            ))
        }
    }

    suspend fun closeSelling(phone: String, channelCode: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.closeSelling(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode
            ))
        }
    }

    suspend fun payChannelList(sellStatus: String = "", buyStatus: String = ""): ApiResult<List<PayChannelResp>> {
        return safeRequest {
            val body = if (sellStatus.isNotEmpty()) {
                buildJsonBody(
                    "sellStatus" to sellStatus
                )
            } else if (buyStatus.isNotEmpty()) {
                buildJsonBody(
                    "buyStatus" to buyStatus
                )
            } else {
                buildJsonBody()
            }
            walletService.payChannelList(body)
        }
    }

    suspend fun getPermissionStatus(phone: String, channelCode: String): ApiResult<ApplyPermissionResp> {
        return safeRequest {
            walletService.getPermissionStatus(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode
            ))
        }
    }

    suspend fun loginWallet(phone: String, channelCode: String, otp: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.loginWallet(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode,
                "otp" to otp
            ))
        }
    }

    suspend fun getUpiList(phone: String, channelCode: String): ApiResult<List<String>> {
        return safeRequest {
            walletService.getUpiList(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode
            ))
        }
    }

    suspend fun addWallet(phone: String, channelCode: String, otp: String, pin: String, upiId: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.addWallet(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode,
                "otp" to otp,
                "pin" to pin,
                "upiId" to upiId
            ))
        }
    }

    suspend fun editUpi(phone: String, channelCode: String, newUpi: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.editUpi(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode,
                "newUpi" to newUpi
            ))
        }
    }

    suspend fun deAuthorize(phone: String, channelCode: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.deAuthorize(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode
            ))
        }
    }

    suspend fun relink(phone: String, channelCode: String): ApiResult<Boolean> {
        return safeRequest {
            walletService.relink(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode
            ))
        }
    }
}
