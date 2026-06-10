package com.hlc.lib_base.router

/**
 * 跨模块路由参数 key（与 app 模块 OrderActivity 等约定一致）
 */
object RouterParams {
    /** OrderActivity ViewPager 下标：0=INR，1=USDT */
    const val ORDER_TAB_INDEX = "order_tab_index"

    /** 打开订单页时顺带关闭充值页（USDT 收银台 / 支付详情返回场景） */
    const val CLEAR_DEPOSIT_ON_OPEN = "clear_deposit_on_open"
}
