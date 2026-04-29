package com.hlc.mywallet

import android.app.Application
import com.hjq.toast.Toaster
import dagger.hilt.android.HiltAndroidApp
import me.jessyan.autosize.AutoSizeConfig
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initAutoSize()
        initToaster()
    }
    
    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
    
    private fun initAutoSize() {
        AutoSizeConfig.getInstance()
            .setDesignWidthInDp(375)  // 设计稿宽度 375dp
            .setDesignHeightInDp(812) // 设计稿高度 812dp
            .setLog(BuildConfig.DEBUG)
    }
    
    private fun initToaster() {
        Toaster.init(this)
        // 设置 Toast 样式
        Toaster.setDebugMode(BuildConfig.DEBUG)
    }
}
