package com.hlc.mywallet.common

import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent

/**
 * Adjust 自定义事件追踪入口
 */
object AdjustTracker {

    /**
     * 注册事件 Adjust event token
     */
    private const val EVENT_TOKEN_REGISTER = "5aeuji"

    /**
     * 绑定 UPI 事件 Adjust event token
     */
    private const val EVENT_TOKEN_BIND_UPI = "rp44yv"

    /**
     * 用户注册成功
     */
    fun trackRegister() {
        trackEvent(EVENT_TOKEN_REGISTER)
    }

    /**
     * 用户绑定 UPI / 添加钱包成功
     */
    fun trackBindUpi() {
        trackEvent(EVENT_TOKEN_BIND_UPI)
    }

    private fun trackEvent(token: String) {
        if (token.isBlank()) {
            return
        }
//        val event = AdjustEvent(token)
//        Adjust.trackEvent(event)
    }
}
