package com.hlc.mywallet.router

import com.hlc.lib_base.router.Router
import com.hlc.lib_base.web.WebActivity
import com.hlc.mywallet.feature.bonus.BonusCenterActivity
import com.hlc.mywallet.feature.deposit.DepositActivity
import com.hlc.mywallet.feature.main.MainActivity
import com.hlc.mywallet.feature.deposit.PaymentDetailActivity
import com.hlc.mywallet.feature.home.HomeActivity
import com.hlc.mywallet.feature.mine.login.LoginActivity
import com.hlc.mywallet.feature.main.customer_service.CustomerServiceActivity
import com.hlc.mywallet.feature.mine.BindTgActivity
import com.hlc.mywallet.feature.mine.PersonalActivity
import com.hlc.mywallet.feature.mine.bills.BillsActivity
import com.hlc.mywallet.feature.mine.order.OrderActivity
import com.hlc.mywallet.feature.mine.PinActivity
import com.hlc.mywallet.feature.mine.SetPasswordActivity
import com.hlc.mywallet.feature.mine.register.RegisterActivity
import com.hlc.mywallet.feature.team.SublineActivity
import com.hlc.mywallet.feature.tutorial.TutorialActivity
import com.hlc.mywallet.feature.wallet.EditUpiActivity
import com.hlc.mywallet.feature.wallet.PayChannelActivity
import com.hlc.mywallet.feature.wallet.PayChannelSetupActivity
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
                Routes.REGISTER to RegisterActivity::class.java,
                Routes.TUTORIAL_LIST to TutorialActivity::class.java,
                Routes.PAYMENT_DETAIL to PaymentDetailActivity::class.java,
                Routes.DEPOSIT to DepositActivity::class.java,
                Routes.WEB to WebActivity::class.java,
                Routes.DEPOSIT_ORDER_LIST to OrderActivity::class.java,
                Routes.BILLS to BillsActivity::class.java,
                Routes.PAY_CHANNEL to PayChannelActivity::class.java,
                Routes.BIND_TG to BindTgActivity::class.java,
                Routes.PIN to PinActivity::class.java,
                Routes.PAY_CHANNEL_SETUP to PayChannelSetupActivity::class.java,
                Routes.EDIT_UPI to EditUpiActivity::class.java,
                Routes.BONUS_CENTER to BonusCenterActivity::class.java,
                Routes.SUBLINE to SublineActivity::class.java,
                Routes.PERSONAL to PersonalActivity::class.java,
                Routes.SET_PASSWORD to SetPasswordActivity::class.java,
                Routes.CUSTOMER_SERVICE to CustomerServiceActivity::class.java
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
