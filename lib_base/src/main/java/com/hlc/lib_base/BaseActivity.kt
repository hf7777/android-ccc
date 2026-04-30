package com.hlc.lib_base

import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.Toaster
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading

abstract class BaseActivity(@LayoutRes layoutResId: Int) : AppCompatActivity(layoutResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initImmersionBar()
        initView()
        initData()
        observeData()
    }

    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
    }

    /**
     * 初始化沉浸式状态栏，子类可重写自定义
     */
    protected open fun initImmersionBar() {
        ImmersionBarHelper.initDefault(this)
    }

    protected open fun initView() = Unit

    protected open fun initData() = Unit

    protected open fun observeData() = Unit


    protected fun showError(message: String) {
        if (isFinishing || isDestroyed) return
        Toaster.show(message)
    }

    /**
     * 设置状态栏颜色
     */
    protected fun setStatusBarColor(@ColorInt color: Int, darkFont: Boolean = true) {
        ImmersionBarHelper.initWithColor(this, color, darkFont)
    }

    /**
     * 设置状态栏颜色资源
     */
    protected fun setStatusBarColorRes(@ColorRes colorRes: Int, darkFont: Boolean = true) {
        ImmersionBarHelper.initWithColorRes(this, colorRes, darkFont)
    }

    /**
     * 设置透明状态栏
     */
    protected fun setTransparentStatusBar(darkFont: Boolean = true) {
        ImmersionBarHelper.initTransparent(this, darkFont)
    }
}
