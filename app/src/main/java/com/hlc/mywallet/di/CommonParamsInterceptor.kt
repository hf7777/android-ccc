package com.hlc.mywallet.di

import android.content.Context
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 公共参数拦截器
 * 自动添加 deviceId、versionCode、versionName 到请求头
 */
class CommonParamsInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        // TODO: 后续优化DeviceId
//        DeviceUtils.getAndroidID()
//        重置规则：
//        恢复出厂设置会重置
//        Android 8.0+ 每个应用的 Android ID 不同（应用隔离）
//        卸载重装会变化（Android 8.0+）
        val newRequest = originalRequest.newBuilder()
            .addHeader("deviceId", DeviceUtils.getAndroidID())
            .addHeader("versionCode", AppUtils.getAppVersionCode().toString())
            .addHeader("versionName", AppUtils.getAppVersionName())
            .build()
        
        return chain.proceed(newRequest)
    }
}
