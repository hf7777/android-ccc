package com.hlc.mywallet.fcm

import android.content.Context
import com.hlc.lib_base.router.Router
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.router.Routes

/**
 * 执行 FCM 通知点击后的页面跳转
 */
object FcmNotificationRouter {

    /**
     * 根据解析出的行为执行跳转
     */
    fun route(context: Context, action: FcmNotificationAction) {
        when (action) {
            FcmNotificationAction.OpenBillsWithdrawal -> navigateToBillsWithdrawal(context)
            FcmNotificationAction.Default -> navigateToMain(context)
        }
    }

    private fun navigateToBillsWithdrawal(context: Context) {
        Router.navigation(Routes.BILLS)
            .with(Constants.RouterKeys.DEFAULT_TO_WITHDRAWAL, true)
            .navigation(context)
    }

    private fun navigateToMain(context: Context) {
        Router.navigation(Routes.MAIN).navigation(context)
    }
}
