package com.hlc.lib_base.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.blankj.utilcode.util.BarUtils
import com.hlc.lib_base.R
import com.hlc.lib_base.databinding.LayoutTitleBarBinding
import com.hlc.lib_base.extension.onClick

/**
 * 通用标题栏
 * 支持沉浸式状态栏适配
 */
class TitleBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutTitleBarBinding =
        LayoutTitleBarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL
        
        // 读取自定义属性
        context.obtainStyledAttributes(attrs, R.styleable.TitleBar).apply {
            try {
                // 标题
                val title = getString(R.styleable.TitleBar_title)
                setTitle(title)
                
                // 标题颜色
                val titleColor = getColor(R.styleable.TitleBar_titleColor, Color.WHITE)
                setTitleColor(titleColor)
                
                // 背景色
                val bgColor = getColor(R.styleable.TitleBar_backgroundColor, context.getColor(R.color.theme))
                setBackgroundColor(bgColor)
                
                // 返回按钮
                val showBack = getBoolean(R.styleable.TitleBar_showBack, true)
                setBackVisible(showBack)
                
                // 右侧文字
                val rightText = getString(R.styleable.TitleBar_rightText)
                setRightText(rightText)
                
            } finally {
                recycle()
            }
        }
    }

    /**
     * 设置沉浸式状态栏
     * @param immersive 是否启用沉浸式
     * @param statusBarColor 状态栏颜色，默认与标题栏背景色一致
     */
    fun setImmersive(immersive: Boolean, @ColorInt statusBarColor: Int? = null) {
        if (immersive) {
            val statusBarHeight = BarUtils.getStatusBarHeight()
            binding.viewStatusBar.layoutParams = binding.viewStatusBar.layoutParams.apply {
                height = statusBarHeight
            }
            // 设置状态栏占位背景色
            statusBarColor?.let {
                binding.viewStatusBar.setBackgroundColor(it)
            }
        } else {
            binding.viewStatusBar.layoutParams = binding.viewStatusBar.layoutParams.apply {
                height = 0
            }
        }
    }

    /**
     * 设置标题
     */
    fun setTitle(title: String?) {
        binding.tvTitle.text = title ?: ""
    }

    /**
     * 设置标题颜色
     */
    fun setTitleColor(@ColorInt color: Int) {
        binding.tvTitle.setTextColor(color)
    }

    /**
     * 设置背景色
     */
    override fun setBackgroundColor(@ColorInt color: Int) {
        binding.rlTitleBar.setBackgroundColor(color)
        binding.viewStatusBar.setBackgroundColor(color)
    }

    /**
     * 设置返回按钮可见性
     */
    fun setBackVisible(visible: Boolean) {
        binding.ivBack.visibility = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * 设置返回按钮图标
     */
    fun setBackIcon(@DrawableRes resId: Int) {
        binding.ivBack.setImageResource(resId)
    }

    /**
     * 设置返回按钮点击事件
     */
    fun setOnBackClickListener(listener: OnClickListener) {
        binding.ivBack.setOnClickListener { listener.onClick(it) }
    }

    /**
     * 设置右侧文字
     */
    fun setRightText(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.tvRight.visibility = View.GONE
        } else {
            binding.tvRight.visibility = View.VISIBLE
            binding.tvRight.text = text
        }
    }

    /**
     * 设置右侧文字点击事件
     */
    fun setOnRightTextClickListener(listener: OnClickListener) {
        binding.tvRight.setOnClickListener { listener.onClick(it) }
    }

    /**
     * 设置右侧图标
     */
    fun setRightIcon(@DrawableRes resId: Int) {
        binding.ivRight.visibility = View.VISIBLE
        binding.ivRight.setImageResource(resId)
    }

    /**
     * 设置右侧图标点击事件
     */
    fun setOnRightIconClickListener(listener: OnClickListener) {
        binding.ivRight.setOnClickListener { listener.onClick(it) }
    }

    /**
     * 隐藏右侧图标
     */
    fun hideRightIcon() {
        binding.ivRight.visibility = View.GONE
    }
}
