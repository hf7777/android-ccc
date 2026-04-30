package com.hlc.lib_base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * 全局 Application Context 持有者
 * 在 Application.onCreate() 中初始化
 */
object AppContext {
    
    @SuppressLint("StaticFieldLeak")
    private var application: Application? = null
    
    /**
     * 初始化，在 Application.onCreate() 中调用
     */
    fun init(app: Application) {
        application = app
    }
    
    /**
     * 获取全局 Application Context
     */
    fun get(): Context {
        return application ?: throw IllegalStateException(
            "AppContext not initialized. Call AppContext.init(this) in Application.onCreate()"
        )
    }
    
    /**
     * 获取 Application 实例
     */
    fun getApp(): Application {
        return application ?: throw IllegalStateException(
            "AppContext not initialized. Call AppContext.init(this) in Application.onCreate()"
        )
    }
}
