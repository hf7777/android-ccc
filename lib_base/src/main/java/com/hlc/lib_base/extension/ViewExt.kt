package com.hlc.lib_base.extension

import android.view.View

/**
 * View 扩展函数
 */

/**
 * 防抖点击
 * @param interval 防抖间隔时间（毫秒），默认 500ms
 * @param action 点击事件
 */
fun View.setOnClickListener(interval: Long = 500, action: (View) -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime = 0L
        
        override fun onClick(v: View) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > interval) {
                lastClickTime = currentTime
                action(v)
            }
        }
    })
}

/**
 * 防抖点击（无参数版本）
 * @param interval 防抖间隔时间（毫秒），默认 500ms
 * @param action 点击事件
 */
inline fun View.onClick(interval: Long = 500, crossinline action: () -> Unit) {
    setOnClickListener(interval) { action() }
}

/**
 * 设置多个 View 的防抖点击
 * @param interval 防抖间隔时间（毫秒），默认 500ms
 * @param action 点击事件，参数为被点击的 View
 */
fun setOnClickListener(vararg views: View, interval: Long = 500, action: (View) -> Unit) {
    views.forEach { view ->
        view.setOnClickListener(interval, action)
    }
}

/**
 * 设置多个 View 的防抖点击（无参数版本）
 * @param interval 防抖间隔时间（毫秒），默认 500ms
 * @param action 点击事件
 */
inline fun setOnClick(vararg views: View, interval: Long = 500, crossinline action: () -> Unit) {
    views.forEach { view ->
        view.onClick(interval, action)
    }
}

/**
 * 显示 View
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * 隐藏 View（占位）
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * 隐藏 View（不占位）
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * 切换 View 可见性
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

/**
 * 根据条件显示或隐藏 View
 * @param show true 显示，false 隐藏
 * @param useGone true 使用 GONE，false 使用 INVISIBLE
 */
fun View.visibleOrGone(show: Boolean, useGone: Boolean = true) {
    visibility = if (show) {
        View.VISIBLE
    } else {
        if (useGone) View.GONE else View.INVISIBLE
    }
}

/**
 * 判断 View 是否可见
 */
fun View.isVisible(): Boolean = visibility == View.VISIBLE

/**
 * 判断 View 是否隐藏
 */
fun View.isGone(): Boolean = visibility == View.GONE

/**
 * 判断 View 是否不可见
 */
fun View.isInvisible(): Boolean = visibility == View.INVISIBLE

/**
 * 启用 View
 */
fun View.enable() {
    isEnabled = true
}

/**
 * 禁用 View
 */
fun View.disable() {
    isEnabled = false
}

/**
 * 设置 View 是否可用
 */
fun View.setEnable(enable: Boolean) {
    isEnabled = enable
}
