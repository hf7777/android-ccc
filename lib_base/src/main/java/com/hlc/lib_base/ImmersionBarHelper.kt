package com.hlc.lib_base

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar

/**
 * ImmersionBar 封装工具类
 */
object ImmersionBarHelper {

    /**
     * 默认配置：白色状态栏，深色图标
     */
    fun initDefault(activity: FragmentActivity) {
        activity.immersionBar {
            statusBarColorInt(Color.WHITE)
            statusBarDarkFont(true)
            navigationBarColorInt(Color.WHITE)
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }

    /**
     * 透明状态栏
     */
    fun initTransparent(activity: FragmentActivity, darkFont: Boolean = true) {
        activity.immersionBar {
            transparentStatusBar()
            statusBarDarkFont(darkFont)
            navigationBarColorInt(Color.WHITE)
            navigationBarDarkIcon(true)
            fitsSystemWindows(false)
        }
    }

    /**
     * 自定义颜色状态栏
     */
    fun initWithColor(
        activity: FragmentActivity,
        @ColorInt statusBarColor: Int,
        darkFont: Boolean = true
    ) {
        activity.immersionBar {
            statusBarColorInt(statusBarColor)
            statusBarDarkFont(darkFont)
            navigationBarColorInt(Color.WHITE)
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }

    /**
     * 自定义颜色资源状态栏
     */
    fun initWithColorRes(
        activity: FragmentActivity,
        @ColorRes statusBarColorRes: Int,
        darkFont: Boolean = true
    ) {
        activity.immersionBar {
            statusBarColor(statusBarColorRes)
            statusBarDarkFont(darkFont)
            navigationBarColorInt(Color.WHITE)
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }

    /**
     * Fragment 默认配置
     */
    fun initDefault(fragment: Fragment) {
        fragment.immersionBar {
            statusBarColorInt(Color.WHITE)
            statusBarDarkFont(true)
            navigationBarColorInt(Color.WHITE)
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }

    /**
     * Fragment 透明状态栏
     */
    fun initTransparent(fragment: Fragment, darkFont: Boolean = true) {
        fragment.immersionBar {
            transparentStatusBar()
            statusBarDarkFont(darkFont)
            navigationBarColorInt(Color.WHITE)
            navigationBarDarkIcon(true)
            fitsSystemWindows(false)
        }
    }
}

/**
 * Activity 扩展函数
 */
fun FragmentActivity.initImmersionBar(
    @ColorInt statusBarColor: Int = Color.WHITE,
    darkFont: Boolean = true
) {
    ImmersionBarHelper.initWithColor(this, statusBarColor, darkFont)
}

fun FragmentActivity.initImmersionBarRes(
    @ColorRes statusBarColorRes: Int,
    darkFont: Boolean = true
) {
    ImmersionBarHelper.initWithColorRes(this, statusBarColorRes, darkFont)
}

fun FragmentActivity.initTransparentBar(darkFont: Boolean = true) {
    ImmersionBarHelper.initTransparent(this, darkFont)
}

/**
 * Fragment 扩展函数
 */
fun Fragment.initImmersionBar() {
    ImmersionBarHelper.initDefault(this)
}

fun Fragment.initTransparentBar(darkFont: Boolean = true) {
    ImmersionBarHelper.initTransparent(this, darkFont)
}
