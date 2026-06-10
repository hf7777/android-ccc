package com.hlc.mywallet.feature.wallet

import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.lib_base.net.safeRequestWithoutData
import com.hlc.mywallet.data.api.WalletService
import com.hlc.mywallet.data.model.req.BankInfoReq
import com.hlc.mywallet.data.model.req.WalletBankInfoReq
import com.hlc.mywallet.data.model.resp.ApplyPermissionResp
import com.hlc.mywallet.data.model.resp.PayChannelResp
import com.hlc.mywallet.data.model.resp.InstallGuide
import com.hlc.mywallet.data.model.resp.OtpActiveStatus
import com.hlc.mywallet.data.model.resp.WalletBankInfoResp
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

    suspend fun payChannelList(sellStatus: String = "", buyStatus: String = "", autoBuyStatus: String = ""): ApiResult<List<PayChannelResp>> {
        return safeRequest {
            val body = if (sellStatus.isNotEmpty()) {
                buildJsonBody(
                    "sellStatus" to sellStatus
                )
            } else if (buyStatus.isNotEmpty()) {
                buildJsonBody(
                    "buyStatus" to buyStatus
                )
            } else if (autoBuyStatus.isNotEmpty()) {
                buildJsonBody(
                    "autoBuyStatus" to autoBuyStatus
                )
            }
            else {
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

    suspend fun walletVersion(): ApiResult<String> {
        return safeRequest {
            walletService.walletVersion()
        }
    }

    suspend fun moduleGuideList(channelCode: String): ApiResult<List<InstallGuide>> {
        return safeRequest {
            walletService.moduleGuideList(channelCode)
        }
    }

    suspend fun autoActiveStatus(phone: String, channelCode: String): ApiResult<OtpActiveStatus> {
        return safeRequest {
            walletService.autoActiveStatus(
                buildJsonBody(
                    "phone" to phone,
                    "channelCode" to channelCode
                )
            )
        }
    }

    suspend fun addBankCard(bankInfoReq: BankInfoReq): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.addBankCard(bankInfoReq)
        }
    }

    suspend fun editBankCard(bankInfoReq: BankInfoReq): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.editBankCard(bankInfoReq)
        }
    }

    suspend fun checkUpiPin(req: WalletBankInfoReq): ApiResult<String> {
        return safeRequest {
            walletService.checkUpiPin(req)
        }
    }

    suspend fun walletBankCardList(walletId: String): ApiResult<List<WalletBankInfoResp>> {
        return safeRequest {
            walletService.walletBankCardList(walletId)
        }
    }

    suspend fun walletBankCardDetail(bankCardId: String): ApiResult<WalletBankInfoResp> {
        return safeRequest {
            walletService.walletBankCardDetail(bankCardId)
        }
    }

    suspend fun addWalletBankCard(id: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.addWalletBankCard(
                buildJsonBody(
                    "id" to id
                )
            )
        }
    }

    suspend fun editWalletBankCard(id: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            walletService.editWalletBankCard(buildJsonBody(
                "id" to id
            ))
        }
    }
}
