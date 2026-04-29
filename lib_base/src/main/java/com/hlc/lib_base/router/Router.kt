package com.hlc.lib_base.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.LogUtils

/**
 * 路由管理器
 * 支持页面跳转、参数传递、自定义协议跳转
 */
object Router {
    
    private val routes = mutableMapOf<String, Class<out Activity>>()
    private val interceptors = mutableListOf<RouterInterceptor>()
    private val schemeHandlers = mutableMapOf<String, SchemeHandler>()
    
    /**
     * 注册路由
     */
    fun register(path: String, activityClass: Class<out Activity>) {
        routes[path] = activityClass
    }
    
    /**
     * 批量注册路由
     */
    fun register(routes: Map<String, Class<out Activity>>) {
        this.routes.putAll(routes)
    }
    
    /**
     * 添加拦截器
     */
    fun addInterceptor(interceptor: RouterInterceptor) {
        interceptors.add(interceptor)
    }
    
    /**
     * 注册协议处理器
     */
    fun registerScheme(scheme: String, handler: SchemeHandler) {
        schemeHandlers[scheme] = handler
    }
    
    /**
     * 开始导航
     */
    fun navigation(path: String): NavigationBuilder {
        return NavigationBuilder(path)
    }
    
    /**
     * 直接跳转（无参数）
     */
    fun navigation(context: Context, path: String) {
        navigation(path).navigation(context)
    }
    
    /**
     * 导航构建器
     */
    class NavigationBuilder(private val path: String) {
        private val bundle = Bundle()
        private var requestCode: Int? = null
        private var flags: Int? = null
        private var enterAnim: Int? = null
        private var exitAnim: Int? = null
        
        /**
         * 添加参数
         */
        fun with(key: String, value: Any?): NavigationBuilder {
            when (value) {
                null -> bundle.putString(key, null)
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Float -> bundle.putFloat(key, value)
                is Double -> bundle.putDouble(key, value)
                is Bundle -> bundle.putBundle(key, value)
                is ArrayList<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    when {
                        value.isEmpty() -> bundle.putStringArrayList(key, value as ArrayList<String>)
                        value[0] is String -> bundle.putStringArrayList(key, value as ArrayList<String>)
                        value[0] is Int -> bundle.putIntegerArrayList(key, value as ArrayList<Int>)
                        else -> LogUtils.w("Unsupported ArrayList type: ${value[0]?.javaClass}")
                    }
                }
                else -> LogUtils.w("Unsupported parameter type: ${value.javaClass}")
            }
            return this
        }
        
        /**
         * 批量添加参数
         */
        fun withBundle(bundle: Bundle): NavigationBuilder {
            this.bundle.putAll(bundle)
            return this
        }
        
        /**
         * 设置 requestCode（用于 startActivityForResult）
         */
        fun withRequestCode(requestCode: Int): NavigationBuilder {
            this.requestCode = requestCode
            return this
        }
        
        /**
         * 设置 Intent Flags
         */
        fun withFlags(flags: Int): NavigationBuilder {
            this.flags = flags
            return this
        }
        
        /**
         * 设置转场动画
         */
        fun withTransition(enterAnim: Int, exitAnim: Int): NavigationBuilder {
            this.enterAnim = enterAnim
            this.exitAnim = exitAnim
            return this
        }
        
        /**
         * 执行导航
         */
        fun navigation(context: Context): Boolean {
            return navigation(context, null)
        }
        
        /**
         * 从 Fragment 执行导航
         */
        fun navigation(fragment: Fragment): Boolean {
            return navigation(fragment.requireContext(), fragment)
        }
        
        private fun navigation(context: Context, fragment: Fragment?): Boolean {
            try {
                // 处理自定义协议
                if (path.contains("://")) {
                    return handleScheme(context, path)
                }
                
                // 执行拦截器
                val request = RouterRequest(path, bundle)
                for (interceptor in interceptors) {
                    if (!interceptor.intercept(context, request)) {
                        LogUtils.d("Navigation intercepted by ${interceptor.javaClass.simpleName}")
                        return false
                    }
                }
                
                // 查找目标 Activity
                val targetClass = routes[path]
                if (targetClass == null) {
                    LogUtils.e("Route not found: $path")
                    return false
                }
                
                // 创建 Intent
                val intent = Intent(context, targetClass).apply {
                    putExtras(bundle)
                    flags?.let { addFlags(it) }
                }
                
                // 启动 Activity
                when {
                    requestCode != null && fragment != null -> {
                        fragment.startActivityForResult(intent, requestCode!!)
                    }
                    requestCode != null && context is Activity -> {
                        context.startActivityForResult(intent, requestCode!!)
                    }
                    else -> {
                        if (context !is Activity) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                }
                
                // 设置转场动画
                if (enterAnim != null && exitAnim != null && context is Activity) {
                    context.overridePendingTransition(enterAnim!!, exitAnim!!)
                }
                
                return true
            } catch (e: Exception) {
                LogUtils.e("Navigation failed: $path", e)
                return false
            }
        }
        
        /**
         * 处理自定义协议
         */
        private fun handleScheme(context: Context, url: String): Boolean {
            val uri = Uri.parse(url)
            val scheme = uri.scheme ?: return false
            
            // 处理 http/https
            if (scheme == "http" || scheme == "https") {
                return openBrowser(context, url)
            }
            
            // 处理自定义协议
            val handler = schemeHandlers[scheme]
            if (handler != null) {
                return handler.handle(context, uri, bundle)
            }
            
            LogUtils.w("No handler for scheme: $scheme")
            return false
        }
        
        /**
         * 打开浏览器
         */
        private fun openBrowser(context: Context, url: String): Boolean {
            return try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                LogUtils.e("Failed to open browser: $url", e)
                false
            }
        }
    }
}
