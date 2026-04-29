package com.hlc.mywallet

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.hjq.toast.Toaster
import dagger.hilt.android.HiltAndroidApp
import me.jessyan.autosize.AutoSizeConfig
import com.blankj.utilcode.util.LogUtils

@HiltAndroidApp
class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
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
        com.hlc.mywallet.router.RouterConfig.init()
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
