package com.hlc.mywallet.extension

import android.graphics.Typeface
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.hlc.lib_base.extension.dp
import com.hlc.mywallet.R

/**
 * 在输入框右侧显示眼睛图标，点击切换密码可见/不可见。
 * 支持文本密码与数字密码（numberPassword）输入类型。
 */
fun EditText.setupPasswordVisibilityToggle(
    @DrawableRes closedRes: Int = R.drawable.ic_eye_close,
    @DrawableRes openRes: Int = R.drawable.ic_eye_open
) {
    if (getTag(R.id.tag_password_toggle_setup) == true) return
    setTag(R.id.tag_password_toggle_setup, true)

    val closed = ContextCompat.getDrawable(context, closedRes)!!.mutate()
    val open = ContextCompat.getDrawable(context, openRes)!!.mutate()
    val iconW = maxOf(closed.intrinsicWidth, 1)
    val iconH = maxOf(closed.intrinsicHeight, 1)
    closed.setBounds(0, 0, iconW, iconH)
    open.setBounds(0, 0, iconW, iconH)

    val gapPx = (8f * resources.displayMetrics.density).toInt().coerceAtLeast(1)
    compoundDrawablePadding = gapPx

    val extraEnd = 0
    setPaddingRelative(paddingStart, paddingTop, paddingEnd + extraEnd, paddingBottom)

    // 切换 inputType 时系统可能改 typeface（如 number 与 numberPassword），hint 会跟变，需固定为布局初始字体
    val stableTypeface = typeface ?: Typeface.DEFAULT

    val baseInputType = inputType
    val isNumericPassword =
        (baseInputType and InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_NUMBER &&
            (baseInputType and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_NUMBER_VARIATION_PASSWORD

    val isTextPassword =
        (baseInputType and InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_TEXT &&
            (
                (baseInputType and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                    (baseInputType and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                )

    var passwordVisible = false

    fun applyPasswordVisible(visible: Boolean) {
        passwordVisible = visible
        when {
            isNumericPassword -> {
                val withoutClassVariation = baseInputType and
                    InputType.TYPE_MASK_CLASS.inv() and InputType.TYPE_MASK_VARIATION.inv()
                inputType = withoutClassVariation or
                    InputType.TYPE_CLASS_NUMBER or
                    if (visible) {
                        InputType.TYPE_NUMBER_VARIATION_NORMAL
                    } else {
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD
                    }
            }
            isTextPassword -> {
                // 必须先改 inputType，再设 transformation；若再写回 TYPE_TEXT_VARIATION_PASSWORD 会覆盖可见状态
                val withoutClassVariation = baseInputType and
                    InputType.TYPE_MASK_CLASS.inv() and InputType.TYPE_MASK_VARIATION.inv()
                inputType = withoutClassVariation or
                    InputType.TYPE_CLASS_TEXT or
                    if (visible) {
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                transformationMethod = if (visible) {
                    HideReturnsTransformationMethod.getInstance()
                } else {
                    PasswordTransformationMethod.getInstance()
                }
            }
            else -> {
                transformationMethod = if (visible) {
                    HideReturnsTransformationMethod.getInstance()
                } else {
                    PasswordTransformationMethod.getInstance()
                }
            }
        }
        setTypeface(stableTypeface)
        setCompoundDrawablesRelative(null, null, if (visible) open else closed, null)
        val len = text?.length ?: 0
        if (len > 0) setSelection(len)
    }

    applyPasswordVisible(false)

    setOnTouchListener { v, event ->
        val edit = v as EditText
        if (edit.compoundDrawablesRelative[2] == null) return@setOnTouchListener false
        if (event.action != MotionEvent.ACTION_UP) return@setOnTouchListener false

        val rtl = layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL
        val hit = if (rtl) {
            event.x <= edit.compoundPaddingStart
        } else {
            event.x >= edit.width - edit.compoundPaddingEnd
        }
        if (hit) {
            applyPasswordVisible(!passwordVisible)
            return@setOnTouchListener true
        }
        false
    }
}
