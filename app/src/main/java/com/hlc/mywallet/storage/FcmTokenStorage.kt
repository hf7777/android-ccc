package com.hlc.mywallet.storage

import kotlinx.coroutines.flow.Flow

/**
 * FCM 注册令牌持久化接口
 */
interface FcmTokenStorage {
    suspend fun getToken(): String?
    fun getTokenFlow(): Flow<String?>
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}
