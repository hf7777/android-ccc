package com.hlc.mywallet.feature.main

import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.lib_base.net.safeRequestWithoutData
import com.hlc.mywallet.data.api.MainService
import com.hlc.mywallet.data.model.resp.BalanceType
import com.hlc.mywallet.data.model.resp.BindCodeResp
import com.hlc.mywallet.data.model.resp.BulletinResp
import com.hlc.mywallet.data.model.resp.CsUrlResp
import com.hlc.mywallet.data.model.resp.CustomerServiceResp
import com.hlc.mywallet.data.model.resp.SliderCaptchaResp
import com.hlc.mywallet.data.model.resp.VersionResp
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * @author Wade
 * @since 2026/5/12
 */
class MainRepository @Inject constructor(
    private val mainService: MainService
) {

    suspend fun getBindCode(): ApiResult<BindCodeResp> {
        return safeRequest {
            mainService.getBindCode()
        }
    }

    suspend fun sendPinOtp(): ApiResult<Unit> {
        return safeRequestWithoutData {
            mainService.sendPinOtp()
        }
    }

    suspend fun sendWithdrawOtp(): ApiResult<Unit> {
        return safeRequestWithoutData {
            mainService.sendWithdrawOtp()
        }
    }

    suspend fun setPin(pin: String, confirmPin: String, otp: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            mainService.setPin(buildJsonBody(
                "pin" to pin,
                "confirmPin" to confirmPin,
                "otp" to otp
            ))
        }
    }

    suspend fun checkPin(pin: String): ApiResult<Boolean> {
        return safeRequest {
            mainService.checkPin(buildJsonBody(
                "pin" to pin
            ))
        }
    }

    suspend fun sendTgOtp(phone: String,channelCode: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            mainService.sendTgOtp(buildJsonBody(
                "phone" to phone,
                "channelCode" to channelCode
            ))
        }
    }

    suspend fun antBalanceType(): ApiResult<List<BalanceType>> {
        return safeRequest {
            mainService.antBalanceType()
        }
    }

    suspend fun sliderCaptcha(): ApiResult<SliderCaptchaResp> {
        return safeRequest {
            mainService.sliderCaptcha()
        }
    }

    suspend fun uploadImage(file: MultipartBody.Part): ApiResult<String> {
        return safeRequest {
            mainService.uploadImage(file)
        }
    }

    suspend fun bulletinList(): ApiResult<List<BulletinResp>> {
        return safeRequest {
            mainService.bulletinList()
        }
    }

    suspend fun confirmBulletin(announcementId: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            mainService.confirmBulletin(
                buildJsonBody(
                    "announcementId" to announcementId
                )
            )
        }
    }

    suspend fun customerService(): ApiResult<List<CustomerServiceResp>> {
        return safeRequest {
            mainService.customerService()
        }
    }

    suspend fun checkUpdate(currentVersionCode: Int): ApiResult<VersionResp> {
        return safeRequest {
            mainService.checkUpdate(currentVersionCode)
        }
    }

    suspend fun getCustomerServiceUrl(): ApiResult<CsUrlResp> {
        return safeRequest {
            mainService.getCustomerServiceUrl()
        }
    }
}
