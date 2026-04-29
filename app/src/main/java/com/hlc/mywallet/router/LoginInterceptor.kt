package com.hlc.mywallet.router

import android.content.Context
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.RouterInterceptor
import com.hlc.lib_base.router.RouterRequest
import com.blankj.utilcode.util.LogUtils

/**
 * 登录拦截器
 * 需要登录的页面会被拦截，跳转到登录页
 */
class LoginInterceptor : RouterInterceptor {
    
    // 需要登录才能访问的页面
    private val loginRequiredPages = setOf(
        Routes.HOME
        // 添加其他需要登录的页面
    )
    
    override fun intercept(context: Context, request: RouterRequest): Boolean {
        // 如果不需要登录，直接放行
        if (!loginRequiredPages.contains(request.path)) {
            return true
        }
        
        // 检查登录状态（这里简化处理，实际应该从 DataStore 或 SharedPreferences 读取）
        val isLoggedIn = checkLoginStatus()
        
        if (!isLoggedIn) {
            LogUtils.d("User not logged in, redirect to login page")
            // 跳转到登录页
            Router.navigation(Routes.LOGIN)
                .with("redirect", request.path) // 登录成功后跳转回原页面
                .navigation(context)
            return false
        }
        
        return true
    }
    
    /**
     * 检查登录状态
     * TODO: 实际项目中应该从存储中读取
     */
    private fun checkLoginStatus(): Boolean {
        // 这里简化处理，实际应该检查 token 是否存在且有效
        return false
    }
}
