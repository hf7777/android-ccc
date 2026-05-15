package com.hlc.mywallet.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 全局 Activity 栈管理工具。
 *
 * 主要职责：
 * 1. 监听应用内 Activity 生命周期，维护当前存活页面集合
 * 2. 提供按 Activity 类型关闭页面的能力
 * 3. 统一承接跨页面关闭需求，避免业务代码互相持有 Activity 引用
 *
 * 设计说明：
 * - 使用 Application.registerActivityLifecycleCallbacks 自动接入，
 *   不需要每个 Activity 手动注册/反注册
 * - 内部通过 WeakReference 持有 Activity，尽量避免额外生命周期泄漏风险
 * - 使用 CopyOnWriteArrayList 保存当前页面快照，便于在遍历关闭时安全修改集合
 *
 * 适用场景：
 * - 支付/授权完成后，按类型关闭某个中间页
 * - 登录失效后统一关闭某些业务页面
 *
 * 注意：
 * - 这里只做“页面管理”，不负责页面间数据传递
 * - 如果只是普通页面跳转，优先使用正常的导航/finish 流程
 */
object ActivityStackManager {

    private val activityRefs = CopyOnWriteArrayList<WeakReference<Activity>>()

    /**
     * 初始化 Activity 栈监听。
     *
     * 只需要在 Application.onCreate() 中调用一次。
     */
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                addActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) = Unit

            override fun onActivityResumed(activity: Activity) = Unit

            override fun onActivityPaused(activity: Activity) = Unit

            override fun onActivityStopped(activity: Activity) = Unit

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

            override fun onActivityDestroyed(activity: Activity) {
                removeActivity(activity)
            }
        })
    }

    /**
     * 关闭指定类型的所有 Activity 实例。
     *
     * 常用于“某个流程结束后，把入口页和中间页一起收掉”。
     */
    fun finishActivities(activityClass: Class<out Activity>) {
        cleanupReleasedReferences()
        activityRefs.forEach { reference ->
            val activity = reference.get() ?: return@forEach
            if (activity.javaClass == activityClass && !activity.isFinishing && !activity.isDestroyed) {
                activity.finish()
            }
        }
    }

    private fun addActivity(activity: Activity) {
        cleanupReleasedReferences()
        val exists = activityRefs.any { it.get() === activity }
        if (!exists) {
            activityRefs.add(WeakReference(activity))
        }
    }

    private fun removeActivity(activity: Activity) {
        activityRefs.removeAll { reference ->
            val target = reference.get()
            target == null || target === activity
        }
    }

    /**
     * 清理已经失效的弱引用，避免列表里残留空壳引用。
     */
    private fun cleanupReleasedReferences() {
        activityRefs.removeAll { it.get() == null }
    }
}
