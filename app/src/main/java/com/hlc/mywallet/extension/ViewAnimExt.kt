package com.hlc.mywallet.extension

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.hlc.mywallet.R

private const val BREATH_MIN_SCALE = 0.92f
private const val BREATH_MAX_SCALE = 1.00f
private const val BREATH_DURATION_MS = 1000L

/**
 * 呼吸感缩放动画（循环放大缩小）。
 * 需在 View 可见时调用；隐藏时请 [stopBreathingScaleAnimation]。
 */
fun View.startBreathingScaleAnimation(
    minScale: Float = BREATH_MIN_SCALE,
    maxScale: Float = BREATH_MAX_SCALE,
    durationMs: Long = BREATH_DURATION_MS,
) {
    stopBreathingScaleAnimation()
    val run = Runnable {
        pivotX = 0f
        pivotY = 0f
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            this,
            PropertyValuesHolder.ofFloat(View.SCALE_X, minScale, maxScale),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, minScale, maxScale),
        ).apply {
            duration = durationMs
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }
        setTag(R.id.tag_breathing_scale_animator, animator)
        animator.start()
    }
    if (width > 0 && height > 0) run.run() else post(run)
}

fun View.stopBreathingScaleAnimation() {
    (getTag(R.id.tag_breathing_scale_animator) as? ObjectAnimator)?.cancel()
    setTag(R.id.tag_breathing_scale_animator, null)
    animate().cancel()
    scaleX = 1f
    scaleY = 1f
}

/**
 * 快速缩放点击反馈：放大后缩回原尺寸
 */
fun View.playQuickScaleAnim(
    peakScale: Float = 1.12f,
    expandDurationMs: Long = 150L,
    shrinkDurationMs: Long = 170L,
) {
    animate().cancel()
    pivotX = width / 2f
    pivotY = height / 2f
    scaleX = 1f
    scaleY = 1f

    animate()
        .scaleX(peakScale)
        .scaleY(peakScale)
        .setDuration(expandDurationMs)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(shrinkDurationMs)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
        .start()
}
