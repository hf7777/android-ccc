package com.hlc.lib_base.extension

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * 优化 ViewPager2 横向滑动灵敏度，减少纵向滑动时误触发切页。
 */
fun ViewPager2.optimizeSwipeSensitivity(multiplier: Int = 2) {
    if (multiplier <= 1) return

    val recyclerView = getChildAt(0) as? RecyclerView ?: return
    runCatching {
        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val currentTouchSlop = touchSlopField.get(recyclerView) as? Int ?: return
        touchSlopField.set(recyclerView, currentTouchSlop * multiplier)
    }
}
