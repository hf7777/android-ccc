package com.hlc.mywallet.manager

import com.hlc.mywallet.storage.FcmTokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FCM Token 管理器
 * 负责内存缓存 + DataStore 持久化，供拦截器和 FCM Service 使用
 */
@Singleton
class FcmTokenManager @Inject constructor(
    private val storage: FcmTokenStorage
) {
    private val _tokenFlow = MutableStateFlow<String?>(null)
    val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    /**
     * 同步获取当前缓存的 FCM Token，供 OkHttp 拦截器使用
     */
    fun getToken(): String? = _tokenFlow.value

    /**
     * 保存并缓存新的 FCM Token
     */
    suspend fun saveToken(token: String) {
        storage.saveToken(token)
        _tokenFlow.value = token
    }

    /**
     * 从本地恢复 FCM Token 到内存缓存
     */
    suspend fun restoreToken() {
        _tokenFlow.value = storage.getToken()
    }

    suspend fun clearToken() {
        storage.clearToken()
        _tokenFlow.value = null
    }
}
