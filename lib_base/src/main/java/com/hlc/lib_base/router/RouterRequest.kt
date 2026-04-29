package com.hlc.lib_base.router

import android.os.Bundle

/**
 * 路由请求信息
 */
data class RouterRequest(
    val path: String,
    val bundle: Bundle
) {
    /**
     * 获取参数
     */
    fun <T> getParam(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return bundle.get(key) as? T
    }
    
    /**
     * 获取字符串参数
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return bundle.getString(key, defaultValue)
    }
    
    /**
     * 获取整型参数
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return bundle.getInt(key, defaultValue)
    }
    
    /**
     * 获取布尔参数
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return bundle.getBoolean(key, defaultValue)
    }
}
