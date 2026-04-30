package com.hlc.mywallet.router

import com.hlc.lib_base.router.Router
import com.hlc.mywallet.feature.MainActivity
import com.hlc.mywallet.feature.home.HomeActivity
import com.hlc.mywallet.feature.login.LoginActivity
import com.hlc.mywallet.feature.register.RegisterActivity
import com.hlc.mywallet.manager.UserManager

/**
 * 路由配置
 * 在 Application 中初始化
 */
object RouterConfig {
    
    fun init() {
        // 注册路由
        Router.register(
            mapOf(
                Routes.SPLASH to com.hlc.mywallet.feature.splash.SplashActivity::class.java,
                Routes.LOGIN to LoginActivity::class.java,
                Routes.MAIN to MainActivity::class.java,
                Routes.HOME to HomeActivity::class.java,
                Routes.REGISTER to RegisterActivity::class.java
            )
        )
        
        // 注册自定义协议处理器
        Router.registerScheme("action", ActionSchemeHandler())
    }
    
    /**
     * 添加登录拦截器
     * 需要在 Application 中调用，传入 UserManager 实例
     */
    fun addLoginInterceptor(userManager: UserManager) {
        Router.addInterceptor(LoginInterceptor(userManager))
    }
}
