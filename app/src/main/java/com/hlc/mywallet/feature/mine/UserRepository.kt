package com.hlc.mywallet.feature.mine

import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.lib_base.net.safeRequestWithoutData
import com.hlc.mywallet.data.api.UserService
import com.hlc.mywallet.data.model.req.RegisterReq
import com.hlc.mywallet.data.model.resp.CheckBindingResp
import com.hlc.mywallet.data.model.resp.BillsResp
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.data.model.resp.OrderInrResp
import com.hlc.mywallet.data.model.resp.OrderUsdtResp
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import com.hlc.mywallet.data.model.resp.WithdrawRecordResp
import com.hlc.mywallet.data.model.resp.WithdrawStatus
import com.hlc.mywallet.storage.CacheKeys
import com.hlc.mywallet.storage.CacheStorage
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val cacheStorage: CacheStorage
) {
    suspend fun getUserStatistics(): ApiResult<UserStatisticsResp> {
        return safeRequest {
            userService.getUserStatistics()
        }
    }

    suspend fun getMyWallet(): ApiResult<List<MyWalletResp>> {
        val result = safeRequest {
            userService.getMyWallet()
        }
        if (result is ApiResult.Success) {
            cacheStorage.save(CacheKeys.MY_WALLET, result.data)
        }
        return result
    }

    suspend fun getCachedMyWallet(): List<MyWalletResp>? {
        return cacheStorage.getList(CacheKeys.MY_WALLET, MyWalletResp::class.java)
    }

    suspend fun getOrderInrList(page: Int, pageSize: Int,yearMonth: String? = null): ApiResult<OrderInrResp> {
        return safeRequest {
            userService.getOrderInrList(
                buildJsonBody(
                    "yearMonth" to yearMonth,
                    "pageNum" to page,
                    "pageSize" to pageSize
                )
            )
        }
    }

    suspend fun getUsdtList(page: Int, pageSize: Int, yearMonth: String? = null): ApiResult<OrderUsdtResp> {
        return safeRequest {
            userService.getOrderUsdtList(
                buildJsonBody(
                    "yearMonth" to yearMonth,
                    "pageNum" to page,
                    "pageSize" to pageSize
                )
            )
        }
    }

    suspend fun checkBinding(): ApiResult<CheckBindingResp> {
        return safeRequest {
            userService.checkBinding()
        }
    }

    suspend fun geBills(yearMonth: String? = null, type: String? = null): ApiResult<BillsResp> {
        return safeRequest {
            userService.getBills(
                buildJsonBody(
                    "yearMonth" to yearMonth,
                    "type" to type
                )
            )
        }
    }

    suspend fun resetPassword(phone: String, password: String, confirmPassword: String, otp: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            userService.resetPassword(
                buildJsonBody(
                    "phone" to phone,
                    "password" to password,
                    "confirmPassword" to confirmPassword,
                    "otp" to otp
                )
            )
        }
    }

    suspend fun sendPasswordOtp(phone: String, captchaId: String, sliderPosition: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            userService.sendPasswordOtp(
                buildJsonBody(
                    "phone" to phone,
                    "captchaId" to captchaId,
                    "sliderPosition" to sliderPosition
                )
            )
        }
    }

    suspend fun logout(): ApiResult<Unit> {
        return safeRequestWithoutData {
            userService.logout()
        }
    }

    suspend fun register(registerReq: RegisterReq): ApiResult<Unit> {
        return safeRequestWithoutData {
            userService.register(registerReq)
        }
    }

    suspend fun getWithdrawStatus(): ApiResult<WithdrawStatus> {
        return safeRequest {
            userService.getWithdrawStatus()
        }
    }

    suspend fun submitWithdraw(amount: String, pin: String, otp: String): ApiResult<Unit> {
        return safeRequestWithoutData {
            userService.submitWithdraw(
                buildJsonBody(
                    "amount" to amount,
                    "pin" to pin,
                    "otp" to otp
                )
            )
        }
    }

    suspend fun startAutoBuy(): ApiResult<Unit> {
        return safeRequestWithoutData {
            userService.startAutoBuy()
        }
    }

    suspend fun stopAutoBuy(): ApiResult<Unit> {
        return safeRequestWithoutData {
            userService.stopAutoBuy()
        }
    }

    suspend fun getWithdrawRecords(page: Int, pageSize: Int): ApiResult<WithdrawRecordResp> {
        return safeRequest {
            userService.withdrawRecords(
                buildJsonBody(
                    "pageNum" to page,
                    "pageSize" to pageSize
                )
            )
        }
    }
}
