package com.hlc.mywallet

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.hjq.toast.Toaster
import dagger.hilt.android.HiltAndroidApp
import me.jessyan.autosize.AutoSizeConfig
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        try {
            initRouter()
            initUtils()
            initTimber()
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
    
    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
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
