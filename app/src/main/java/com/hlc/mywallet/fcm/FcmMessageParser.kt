package com.hlc.mywallet.fcm

import android.content.Intent
import android.os.Bundle
import com.google.firebase.messaging.RemoteMessage

/**
 * 解析 FCM 消息/Intent 为通知点击行为
 */
object FcmMessageParser {

    private const val KEY_ACTION = "action"
    private const val KEY_FROM_NOTIFICATION = "from_notification"
    private const val ACTION_BILLS_WITHDRAWAL = "bills_withdrawal"

    /**
     * 从 RemoteMessage 中解析行为
     */
    fun parse(message: RemoteMessage): FcmNotificationAction {
        return parse(message.data)
    }

    /**
     * 从启动 Intent 中解析行为
     */
    fun parse(intent: Intent?): FcmNotificationAction {
        return parse(intent?.extras)
    }

    /**
     * 判断启动 Intent 是否来自通知点击
     * 兼容 FCM 系统通知（App 被杀死后收到 notification 消息）和我们自定义通知
     */
    fun isFromNotification(intent: Intent?): Boolean {
        val extras = intent?.extras ?: return false
        return extras.getBoolean(KEY_FROM_NOTIFICATION, false)
            || extras.containsKey("google.message_id")
            || extras.containsKey("google.sent_time")
            || extras.containsKey("collapse_key")
    }

    private fun parse(data: Map<String, String>?): FcmNotificationAction {
        val action = data?.get(KEY_ACTION) ?: ""
        return parseAction(action)
    }

    private fun parse(extras: Bundle?): FcmNotificationAction {
        val action = extras?.getString(KEY_ACTION) ?: ""
        return parseAction(action)
    }

    private fun parseAction(action: String): FcmNotificationAction {
        return when (action) {
            ACTION_BILLS_WITHDRAWAL -> FcmNotificationAction.OpenBillsWithdrawal
            else -> FcmNotificationAction.Default
        }
    }
}
