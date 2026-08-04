package com.hlc.mywallet.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hlc.mywallet.R
import com.hlc.mywallet.feature.splash.SplashActivity
import com.hlc.mywallet.manager.FcmTokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * FCM 消息服务
 * 负责接收新 token 和推送消息
 */
@AndroidEntryPoint
class FcmMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        LogUtils.d("FCM new token: $token")
        serviceScope.launch {
            fcmTokenManager.saveToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        LogUtils.d("FCM message received: messageId=${message.messageId}, data=${message.data}")

        val action = FcmMessageParser.parse(message)
        val title = message.notification?.title ?: message.data["title"] ?: getString(R.string.app_name)
        val body = message.notification?.body ?: message.data["body"] ?: ""
        if (body.isNotBlank()) {
            showNotification(title, body, action)
        }
    }

    private fun showNotification(title: String, body: String, notificationAction: FcmNotificationAction) {
        val channelId = "default_notification_channel"
        createNotificationChannel(channelId)

        val intent = Intent(this, SplashActivity::class.java).apply {
            setAction(Intent.ACTION_MAIN)
            addCategory(Intent.CATEGORY_LAUNCHER)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtras(notificationAction.toExtras())
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_status_bar_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    private fun FcmNotificationAction.toExtras(): Bundle {
        return Bundle().apply {
            putBoolean("from_notification", true)
            FcmNotificationAction.toActionString(this@toExtras)?.let { actionString ->
                putString("action", actionString)
            }
            // 后续新增 action 时，在此把对应参数写入 extras
            when (this@toExtras) {
                FcmNotificationAction.OpenBillsWithdrawal,
                FcmNotificationAction.Default -> {
                    // 默认兜底，不传额外参数，由 SplashActivity 走默认行为
                }
            }
        }
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
