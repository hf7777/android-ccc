package com.hlc.mywallet.feature.mine.order

import android.app.Activity
import android.content.Intent
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.RouterParams
import com.hlc.mywallet.feature.deposit.DepositActivity
import com.hlc.mywallet.common.ActivityStackManager
import com.hlc.mywallet.router.Routes

/**
 * 订单页统一导航：支付详情 / USDT 收银台返回时落到指定 Tab。
 */
object OrderNavigation {

    const val TAB_INR = 0
    const val TAB_USDT = 1

    fun returnToOrderTab(from: Activity, tabIndex: Int) {
        Router.navigation(Routes.DEPOSIT_ORDER_LIST)
            .with(RouterParams.ORDER_TAB_INDEX, tabIndex)
            .with(RouterParams.CLEAR_DEPOSIT_ON_OPEN, true)
            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .navigation(from)
        ActivityStackManager.finishActivities(DepositActivity::class.java)
        from.finish()
    }
}
