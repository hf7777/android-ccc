package com.hlc.mywallet

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.hjq.toast.Toaster
import com.hlc.lib_base.AppContext
import com.hlc.lib_base.autosize.AutoSizeFoldConfigurator
import com.hlc.lib_base.autosize.FoldScreenUtil
import com.hlc.lib_base.net.UnauthorizedHandler
import com.hlc.lib_base.net.UnauthorizedHandlerHolder
import com.hlc.lib_base.router.Router
import com.hlc.mywallet.common.ActivityStackManager
import com.hlc.mywallet.manager.FcmTokenManager
import com.hlc.mywallet.manager.UserManager
import com.hlc.mywallet.router.RouterConfig
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        // 初始化全局 Context
        AppContext.init(this)

        try {
            initActivityStackManager()
            initRouter()
            initUnauthorizedHandler()
            initUtils()
            initGlideCache()
            initLogUtils()
            initAutoSize()
            initToaster()
//            initAdjust()
            initFcmToken()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel()
    }
    
    private fun initRouter() {
        RouterConfig.init()
        // 添加登录拦截器
        RouterConfig.addLoginInterceptor(userManager)
    }

    private fun initActivityStackManager() {
        ActivityStackManager.init(this)
    }
    
    private fun initUnauthorizedHandler() {
        UnauthorizedHandlerHolder.setHandler(object : UnauthorizedHandler {
            override fun handleUnauthorized() {
                LogUtils.w("Handling 401 unauthorized, redirecting to login")
                Router.navigation(Routes.LOGIN)
                    .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .navigation(this@App)
            }
        })
    }
    
    private fun initUtils() {
        Utils.init(this)
    }

    /**
     * 覆盖安装后资源 id 会重新分配，Glide 磁盘缓存中按旧 id 生成的本地图缩略图会与新图标串位，
     * 导致 ivIcon 等本地图标与映射对不上，故在版本号变化时清一次 Glide 缓存。
     */
    private fun initGlideCache() {
        val lastVersion = SPUtils.getInstance().getInt(KEY_LAST_VERSION_CODE, 0)
        val currentVersion = AppUtils.getAppVersionCode()
        if (lastVersion == currentVersion) return

        // clearMemory 必须在主线程调用
        Glide.get(this).clearMemory()
        appScope.launch {
            withContext(Dispatchers.IO) { Glide.get(this@App).clearDiskCache() }
            SPUtils.getInstance().put(KEY_LAST_VERSION_CODE, currentVersion)
        }
    }
    
    private fun initLogUtils() {
        val config = LogUtils.getConfig()
            .setLogSwitch(BuildConfig.DEBUG)
            .setConsoleSwitch(BuildConfig.DEBUG)
            .setGlobalTag("MyWallet")
            .setLog2FileSwitch(false)
            .setLogHeadSwitch(true)
            .setBorderSwitch(true)
            .setSingleTagSwitch(true)
            .setConsoleFilter(LogUtils.V)
    }
    
    private fun initAutoSize() {
        AutoSizeConfig.getInstance()
            .setDesignWidthInDp(375)
            .setDesignHeightInDp(667)
            .setCustomFragment(true)
            .setOnAdaptListener(object : onAdaptListener {
                override fun onAdaptBefore(target: Any, activity: Activity) {
                    if (activity.isFinishing || !FoldScreenUtil.isWindowManagerReady(activity)) return
                    AutoSizeFoldConfigurator.configure(activity)
                }

                override fun onAdaptAfter(target: Any, activity: Activity) = Unit
            })
            .setLog(BuildConfig.DEBUG)
    }
    
    private fun initToaster() {
        Toaster.init(this)
        Toaster.setDebugMode(BuildConfig.DEBUG)
    }

    private fun initAdjust() {
        val appToken = BuildConfig.ADJUST_APP_TOKEN
        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION
        val config = AdjustConfig(this, appToken, environment)
        config.setLogLevel(LogLevel.VERBOSE)
        Adjust.initSdk(config)
    }

    private fun initFcmToken() {
        appScope.launch {
            fcmTokenManager.restoreToken()
            try {
                val token = withContext(Dispatchers.IO) {
                    FirebaseMessaging.getInstance().token.await()
                }
                fcmTokenManager.saveToken(token)
                LogUtils.d("FCM token initialized: $token")
            } catch (e: Exception) {
                LogUtils.e("FCM token initialization failed: ${e.message}")
            }
        }
    }

    companion object {
        private const val KEY_LAST_VERSION_CODE = "last_version_code"
    }
}
