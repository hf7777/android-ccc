package com.hlc.mywallet.di

import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.hlc.lib_base.AppContext
import com.hlc.lib_base.router.Router
import com.hlc.mywallet.manager.UserManager
import com.hlc.mywallet.router.Routes
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val userManager: UserManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val token = runBlocking { userManager.getToken() }
        
        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        val response = chain.proceed(newRequest)
        
        // 处理 401 未授权，跳转到登录页并清空任务栈
        if (response.code == 401) {
            LogUtils.w("Token expired or invalid, redirecting to login")
            Router.navigation(Routes.LOGIN)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .navigation(AppContext.get())
        }
        
        return response
    }
}
