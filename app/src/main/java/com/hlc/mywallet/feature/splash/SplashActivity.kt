package com.hlc.mywallet.feature.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.LogUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.R
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.navigation
import com.hlc.mywallet.databinding.ActivitySplashBinding
import com.hlc.mywallet.fcm.FcmMessageParser
import com.hlc.mywallet.fcm.FcmNotificationAction
import com.hlc.mywallet.fcm.FcmNotificationRouter
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseVbActivity<ActivitySplashBinding>() {

    private val viewModel: SplashViewModel by viewModels()

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarColorInt(ColorUtils.getColor(R.color.theme))
            navigationBarDarkIcon(false)
            fitsSystemWindows(true)
        }
    }

    override fun initView() {
        binding.tvVersion.text = "v${AppUtils.getAppVersionName()}"

        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            val isLoggedIn = viewModel.checkLoginStatus()
            val notificationAction = FcmMessageParser.parse(intent)
            val fromNotification = FcmMessageParser.isFromNotification(intent)
            LogUtils.d("SplashActivity FCM intent extras: ${intent?.extras}")

            when {
                isLoggedIn && fromNotification && notificationAction != FcmNotificationAction.Default -> {
                    // 先启动 MainActivity 作为返回栈底，再跳转目标页
                    Router.navigation(Routes.MAIN).navigation(this@SplashActivity)
                    FcmNotificationRouter.route(this@SplashActivity, notificationAction)
                }
                isLoggedIn -> {
                    Router.navigation(Routes.MAIN).navigation(this@SplashActivity)
                }
                else -> {
                    Router.navigation(Routes.LOGIN).navigation(this@SplashActivity)
                }
            }

            // 关闭启动页，使用淡出动画
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}
