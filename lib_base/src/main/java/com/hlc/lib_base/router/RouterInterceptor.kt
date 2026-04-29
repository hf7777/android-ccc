package com.hlc.lib_base.router

import android.content.Context

/**
 * 路由拦截器
 * 用于在跳转前进行权限检查、登录验证等
 */
interface RouterInterceptor {
    
    /**
     * 拦截处理
     * @return true 继续跳转，false 拦截跳转
     */
    fun intercept(context: Context, request: RouterRequest): Boolean
}
