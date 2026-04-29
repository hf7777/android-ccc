package com.hlc.mywallet.router

import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.SchemeHandler
import timber.log.Timber

/**
 * Action 协议处理器
 * 处理 action:// 协议跳转
 * 
 * 示例：
 * action://main - 跳转到主页
 * action://home?id=123&name=test - 跳转到首页并携带参数
 */
class ActionSchemeHandler : SchemeHandler {
    
    override fun handle(context: Context, uri: Uri, extras: Bundle): Boolean {
        val host = uri.host ?: return false
        
        Timber.d("Handle action scheme: $uri")
        
        // 根据 host 映射到路由路径
        val path = when (host) {
            "main" -> Routes.MAIN
            "home" -> Routes.HOME
            "login" -> Routes.LOGIN
            else -> {
                Timber.w("Unknown action host: $host")
                return false
            }
        }
        
        // 构建导航
        val builder = Router.navigation(path)
        
        // 解析 URL 参数
        uri.queryParameterNames.forEach { key ->
            val value = uri.getQueryParameter(key)
            if (value != null) {
                builder.with(key, value)
            }
        }
        
        // 添加额外参数
        builder.withBundle(extras)
        
        // 执行跳转
        return builder.navigation(context)
    }
}
