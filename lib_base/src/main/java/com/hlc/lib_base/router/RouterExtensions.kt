package com.hlc.lib_base.router

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.hlc.lib_base.R

/**
 * Activity 扩展函数
 */
fun Activity.navigation(path: String, useDefaultAnim: Boolean = true): Boolean {
    val builder = Router.navigation(path)
    if (useDefaultAnim) {
        builder.withTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    return builder.navigation(this)
}

fun Activity.navigationForResult(path: String, requestCode: Int, useDefaultAnim: Boolean = true) {
    val builder = Router.navigation(path).withRequestCode(requestCode)
    if (useDefaultAnim) {
        builder.withTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    builder.navigation(this)
}

/**
 * Fragment 扩展函数
 */
fun Fragment.navigation(path: String, useDefaultAnim: Boolean = true): Boolean {
    val builder = Router.navigation(path)
    if (useDefaultAnim) {
        builder.withTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    return builder.navigation(this)
}

fun Fragment.navigationForResult(path: String, requestCode: Int, useDefaultAnim: Boolean = true) {
    val builder = Router.navigation(path).withRequestCode(requestCode)
    if (useDefaultAnim) {
        builder.withTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    builder.navigation(this)
}

/**
 * Intent 扩展函数 - 获取路由参数
 */
fun Intent.getRouterString(key: String, defaultValue: String = ""): String {
    return extras?.getString(key, defaultValue) ?: defaultValue
}

fun Intent.getRouterInt(key: String, defaultValue: Int = 0): Int {
    return extras?.getInt(key, defaultValue) ?: defaultValue
}

fun Intent.getRouterLong(key: String, defaultValue: Long = 0L): Long {
    return extras?.getLong(key, defaultValue) ?: defaultValue
}

fun Intent.getRouterBoolean(key: String, defaultValue: Boolean = false): Boolean {
    return extras?.getBoolean(key, defaultValue) ?: defaultValue
}

fun Intent.getRouterFloat(key: String, defaultValue: Float = 0f): Float {
    return extras?.getFloat(key, defaultValue) ?: defaultValue
}

fun Intent.getRouterDouble(key: String, defaultValue: Double = 0.0): Double {
    return extras?.getDouble(key, defaultValue) ?: defaultValue
}
