package com.hlc.mywallet.router

import com.hlc.lib_base.router.Router
import com.hlc.mywallet.feature.MainActivity
import com.hlc.mywallet.feature.home.HomeActivity
import com.hlc.mywallet.feature.login.LoginActivity

/**
 * 路由配置
 * 在 Application 中初始化
 */
object RouterConfig {
    
    fun init() {
        // 注册路由
        Router.register(
            mapOf(
                Routes.LOGIN to LoginActivity::class.java,
                Routes.MAIN to MainActivity::class.java,
                Routes.HOME to HomeActivity::class.java
            )
        )
        
        // 添加拦截器
        Router.addInterceptor(LoginInterceptor())
        
        // 注册自定义协议处理器
        Router.registerScheme("action", ActionSchemeHandler())
    }
}
