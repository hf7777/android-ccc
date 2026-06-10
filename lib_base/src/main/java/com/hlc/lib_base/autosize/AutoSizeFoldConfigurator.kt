package com.hlc.lib_base.autosize

import android.app.Activity
import android.content.res.Configuration
import com.blankj.utilcode.util.LogUtils
import me.jessyan.autosize.AutoSizeConfig

/**
 * 折叠屏 / 多窗口 AutoSize 配置（双设计稿 + 窗口宽高修正）。
 *
 * 设计稿对应关系（项目主设计 375×667dp）：
 * - 小屏 / 小窗口：375 × 667
 * - 大屏 + 大窗口竖屏：698 × 667
 * - 大屏 + 大窗口横屏：667 × 698
 * - 大屏 + 小窗口（分屏等）：与手机稿一致
 */
object AutoSizeFoldConfigurator {

    private const val TAG = "AutoSizeFold"

    private const val PHONE_WIDTH_DP = 375
    private const val PHONE_HEIGHT_DP = 667

    /** 原文 1395×1556 → 约 698×667 */
    private const val LARGE_WINDOW_PORTRAIT_WIDTH_DP = 698
    private const val LARGE_WINDOW_PORTRAIT_HEIGHT_DP = 667

    /** 原文 1556×1395 → 约 667×698 */
    private const val LARGE_WINDOW_LANDSCAPE_WIDTH_DP = 667
    private const val LARGE_WINDOW_LANDSCAPE_HEIGHT_DP = 698

    data class DesignProfile(
        val designWidthInDp: Int,
        val designHeightInDp: Int
    )

    /**
     * 在 [me.jessyan.autosize.onAdaptListener.onAdaptBefore] 或 [android.app.Activity.onConfigurationChanged] 中调用。
     *
     * @param configuration 使用 [Resources.getConfiguration] 传入，禁止在内部访问 [Activity.getResources]
     */
    fun configure(activity: Activity, configuration: Configuration) {
        if (!FoldScreenUtil.isWindowManagerReady(activity)) {
            return
        }
        val config = AutoSizeConfig.getInstance()
        val windowSize = FoldScreenUtil.getWindowSizePx(activity)
        val profile = resolveDesignProfile(configuration, activity)

        val adjustedWidth = adjustDesignForWindow(
            profile.designWidthInDp.toFloat(),
            windowSize.width,
            config.screenWidth
        )
        val adjustedHeight = adjustDesignForWindow(
            profile.designHeightInDp.toFloat(),
            windowSize.height,
            config.screenHeight
        )

        config.setCustomFragment(true)
        config.setDesignWidthInDp(adjustedWidth.toInt())
        config.setDesignHeightInDp(adjustedHeight.toInt())

        config.unitsManager
            .setDesignWidth(windowSize.width.toFloat())
            .setDesignHeight(windowSize.height.toFloat())

        LogUtils.dTag(
            TAG,
            "configure ${activity.javaClass.simpleName} " +
                "largeScreen=${FoldScreenUtil.isLargeScreen(configuration)} " +
                "largeWindow=${FoldScreenUtil.isLargeWindow(activity)} " +
                "window=${windowSize.width}x${windowSize.height} " +
                "design=${adjustedWidth.toInt()}x${adjustedHeight.toInt()}dp"
        )
    }

    fun configure(activity: Activity) {
        configure(activity, activity.applicationContext.resources.configuration)
    }

    fun resolveDesignProfile(configuration: Configuration, activity: Activity): DesignProfile {
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        return if (FoldScreenUtil.isLargeScreen(configuration)) {
            if (FoldScreenUtil.isLargeWindow(activity)) {
                if (isLandscape) {
                    DesignProfile(LARGE_WINDOW_LANDSCAPE_WIDTH_DP, LARGE_WINDOW_LANDSCAPE_HEIGHT_DP)
                } else {
                    DesignProfile(LARGE_WINDOW_PORTRAIT_WIDTH_DP, LARGE_WINDOW_PORTRAIT_HEIGHT_DP)
                }
            } else {
                if (isLandscape) {
                    DesignProfile(LARGE_WINDOW_LANDSCAPE_WIDTH_DP, PHONE_HEIGHT_DP)
                } else {
                    DesignProfile(PHONE_WIDTH_DP, PHONE_HEIGHT_DP)
                }
            }
        } else {
            DesignProfile(PHONE_WIDTH_DP, PHONE_HEIGHT_DP)
        }
    }

    private fun adjustDesignForWindow(designDp: Float, windowPx: Int, screenPx: Int): Float {
        if (designDp <= 0f || windowPx <= 0 || screenPx <= 0) return designDp
        return designDp * screenPx / windowPx
    }
}
