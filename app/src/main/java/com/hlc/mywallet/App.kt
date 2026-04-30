package com.hlc.mywallet

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.hjq.toast.Toaster
import dagger.hilt.android.HiltAndroidApp
import me.jessyan.autosize.AutoSizeConfig
import com.blankj.utilcode.util.LogUtils
import com.hlc.mywallet.manager.UserManager
import com.hlc.mywallet.router.RouterConfig
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    
    @Inject
    lateinit var userManager: UserManager
    
    override fun onCreate() {
        super.onCreate()
        // 初始化全局 Context
        com.hlc.lib_base.AppContext.init(this)
        
        try {
            initRouter()
            initUtils()
            initLogUtils()
            initAutoSize()
            initToaster()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun initRouter() {
        RouterConfig.init()
        // 添加登录拦截器
        RouterConfig.addLoginInterceptor(userManager)
    }
    
    private fun initUtils() {
        Utils.init(this)
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
            .setLog(BuildConfig.DEBUG)
    }
    
    private fun initToaster() {
        Toaster.init(this)
        Toaster.setDebugMode(BuildConfig.DEBUG)
    }
}
