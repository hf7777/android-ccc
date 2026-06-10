package com.hlc.lib_base.autosize

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * 折叠屏 / 多窗口判断工具（参考 CSDN 折叠屏 + AutoSize 适配方案）。
 */
object FoldScreenUtil {

    /** 最小屏宽 ≥ 此值视为大屏（平板、折叠屏内屏等） */
    private const val LARGE_SCREEN_SW_DP = 600

    /**
     * 窗口长边 / 短边 低于此阈值视为「大窗口」（接近方形或超宽，折叠屏展开内屏）。
     * 竖屏手机通常 > 1.8，展开折叠屏内屏可 < 1.6。
     */
    private const val MIN_WINDOW_ASPECT = 1.67f

    data class WindowSize(val width: Int, val height: Int)

    fun isLargeScreen(configuration: Configuration): Boolean {
        return configuration.smallestScreenWidthDp >= LARGE_SCREEN_SW_DP
    }

    /**
     * 多窗口 / 分屏 / 平行视窗下 window 与全屏不等价，需用 window 宽高判断。
     */
    fun isLargeWindow(activity: Activity): Boolean {
        val (longSide, shortSide) = getWindowLongShortPx(activity)
        if (shortSide <= 0) return false
        return longSide / shortSide < MIN_WINDOW_ASPECT
    }

    fun getWindowSizePx(activity: Activity): WindowSize {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && isWindowManagerReady(activity)) {
            return getWindowSizeApi30(activity)
        }
        return getFallbackWindowSize(activity)
    }

    /** Activity.attach 之前 getResources() 会触发适配，此时 windowManager 尚未就绪 */
    fun isWindowManagerReady(activity: Activity): Boolean {
        return activity.windowManager != null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getWindowSizeApi30(activity: Activity): WindowSize {
        val windowManager = activity.windowManager ?: return getFallbackWindowSize(activity)
        val bounds = windowManager.currentWindowMetrics.bounds
        return WindowSize(bounds.width(), bounds.height())
    }

    /** 勿使用 activity.resources，避免在 getResources() 中递归 */
    private fun getFallbackWindowSize(activity: Activity): WindowSize {
        val metrics = activity.applicationContext.resources.displayMetrics
        return WindowSize(metrics.widthPixels, metrics.heightPixels)
    }

    private fun getWindowLongShortPx(activity: Activity): Pair<Float, Float> {
        val window = getWindowSizePx(activity)
        val longSide = maxOf(window.width, window.height).toFloat()
        val shortSide = minOf(window.width, window.height).toFloat()
        return longSide to shortSide
    }
}
