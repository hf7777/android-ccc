package com.hlc.mywallet.manager

import com.hlc.lib_storage.TokenStorage
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户管理器
 * 负责管理用户登录状态、Token、用户信息等
 */
@Singleton
class UserManager @Inject constructor(
    private val tokenStorage: TokenStorage
) {
    // 内存缓存
    private var cachedToken: String? = null
    private var cachedUserInfo: UserInfo? = null
    
    /**
     * 获取 Token
     */
    suspend fun getToken(): String? {
        if (cachedToken == null) {
            cachedToken = tokenStorage.getToken().first()
        }
        return cachedToken
    }
    
    /**
     * 保存 Token
     */
    suspend fun saveToken(token: String) {
        cachedToken = token
        tokenStorage.saveToken(token)
    }
    
    /**
     * 清除 Token
     */
    suspend fun clearToken() {
        cachedToken = null
        tokenStorage.clearToken()
    }
    
    /**
     * 是否已登录
     */
    suspend fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
    
    /**
     * 保存用户信息
     */
    fun saveUserInfo(userInfo: UserInfo) {
        cachedUserInfo = userInfo
    }
    
    /**
     * 获取用户信息
     */
    fun getUserInfo(): UserInfo? {
        return cachedUserInfo
    }
    
    /**
     * 清除用户信息
     */
    fun clearUserInfo() {
        cachedUserInfo = null
    }
    
    /**
     * 退出登录
     */
    suspend fun logout() {
        clearToken()
        clearUserInfo()
    }
}

/**
 * 用户信息
 */
data class UserInfo(
    val userId: String,
    val username: String,
    val nickname: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null
)
