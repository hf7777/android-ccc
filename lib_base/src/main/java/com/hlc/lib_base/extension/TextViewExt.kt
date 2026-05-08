package com.hlc.lib_base.extension

import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun TextView.setDrawablePadding(
    @DrawableRes leftResId: Int = 0,
    @DrawableRes topResId: Int = 0,
    @DrawableRes rightResId: Int = 0,
    @DrawableRes bottomResId: Int = 0,
    leftPadding: Int = 0,
    topPadding: Int = 0,
    rightPadding: Int = 0,
    bottomPadding: Int = 0,
    drawableWidth: Int = 0,
    drawableHeight: Int = 0
) {
    setCompoundDrawableSize(
        leftResId = leftResId,
        topResId = topResId,
        rightResId = rightResId,
        bottomResId = bottomResId,
        leftPadding = leftPadding,
        topPadding = topPadding,
        rightPadding = rightPadding,
        bottomPadding = bottomPadding,
        drawableWidth = drawableWidth,
        drawableHeight = drawableHeight
    )
}

fun TextView.setDrawablePadding(
    left: Drawable? = null,
    top: Drawable? = null,
    right: Drawable? = null,
    bottom: Drawable? = null,
    leftPadding: Int = 0,
    topPadding: Int = 0,
    rightPadding: Int = 0,
    bottomPadding: Int = 0,
    drawableWidth: Int = 0,
    drawableHeight: Int = 0
) {
    setCompoundDrawableSize(
        left = left,
        top = top,
        right = right,
        bottom = bottom,
        leftPadding = leftPadding,
        topPadding = topPadding,
        rightPadding = rightPadding,
        bottomPadding = bottomPadding,
        drawableWidth = drawableWidth,
        drawableHeight = drawableHeight
    )
}

fun TextView.setCompoundDrawableSize(
    @DrawableRes leftResId: Int = 0,
    @DrawableRes topResId: Int = 0,
    @DrawableRes rightResId: Int = 0,
    @DrawableRes bottomResId: Int = 0,
    leftPadding: Int = 0,
    topPadding: Int = 0,
    rightPadding: Int = 0,
    bottomPadding: Int = 0,
    drawableWidth: Int = 0,
    drawableHeight: Int = 0
) {
    setCompoundDrawableSize(
        left = leftResId.takeIf { it != 0 }?.let { ContextCompat.getDrawable(context, it) },
        top = topResId.takeIf { it != 0 }?.let { ContextCompat.getDrawable(context, it) },
        right = rightResId.takeIf { it != 0 }?.let { ContextCompat.getDrawable(context, it) },
        bottom = bottomResId.takeIf { it != 0 }?.let { ContextCompat.getDrawable(context, it) },
        leftPadding = leftPadding,
        topPadding = topPadding,
        rightPadding = rightPadding,
        bottomPadding = bottomPadding,
        drawableWidth = drawableWidth,
        drawableHeight = drawableHeight
    )
}

fun TextView.setCompoundDrawableSize(
    left: Drawable? = null,
    top: Drawable? = null,
    right: Drawable? = null,
    bottom: Drawable? = null,
    leftPadding: Int = 0,
    topPadding: Int = 0,
    rightPadding: Int = 0,
    bottomPadding: Int = 0,
    drawableWidth: Int = 0,
    drawableHeight: Int = 0
) {
    compoundDrawablePadding = 0
    setCompoundDrawablesRelative(
        left?.buildCompoundDrawable(
            width = drawableWidth,
            height = drawableHeight,
            rightInset = leftPadding
        ),
        top?.buildCompoundDrawable(
            width = drawableWidth,
            height = drawableHeight,
            bottomInset = topPadding
        ),
        right?.buildCompoundDrawable(
            width = drawableWidth,
            height = drawableHeight,
            leftInset = rightPadding
        ),
        bottom?.buildCompoundDrawable(
            width = drawableWidth,
            height = drawableHeight,
            topInset = bottomPadding
        )
    )
}

private fun Drawable.buildCompoundDrawable(
    width: Int,
    height: Int,
    leftInset: Int = 0,
    topInset: Int = 0,
    rightInset: Int = 0,
    bottomInset: Int = 0
): Drawable {
    val targetWidth = if (width > 0) width else intrinsicWidth.coerceAtLeast(0)
    val targetHeight = if (height > 0) height else intrinsicHeight.coerceAtLeast(0)
    val drawable = mutate()
    drawable.setBounds(0, 0, targetWidth, targetHeight)

    val insetDrawable = InsetDrawable(drawable, leftInset, topInset, rightInset, bottomInset)
    insetDrawable.setBounds(
        0,
        0,
        targetWidth + leftInset + rightInset,
        targetHeight + topInset + bottomInset
    )
    return insetDrawable
}
