package com.hlc.mywallet.feature.splash

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.R
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.navigation
import com.hlc.mywallet.databinding.ActivitySplashBinding
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
        // 延迟一段时间后检查登录状态
        lifecycleScope.launch {
            delay(1000) // 显示 1 秒启动页
            checkLoginStatus()
        }
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            val isLoggedIn = viewModel.checkLoginStatus()
            
            if (isLoggedIn) {
                // 已登录，跳转到主页
                Router.navigation(Routes.MAIN).navigation(this@SplashActivity)
            } else {
                // 未登录，跳转到登录页
                Router.navigation(Routes.LOGIN).navigation(this@SplashActivity)
            }
            
            // 关闭启动页，使用淡出动画
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}
