package com.hlc.mywallet.feature.mine

import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.mywallet.data.api.UserService
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.data.model.resp.OrderInrResp
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
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

    suspend fun getOrderInrList(yearMonth: String? = null): ApiResult<OrderInrResp> {
        return safeRequest {
            userService.getOrderInrList(
                buildJsonBody(
                    "yearMonth" to yearMonth
                )
            )
        }
    }
}
