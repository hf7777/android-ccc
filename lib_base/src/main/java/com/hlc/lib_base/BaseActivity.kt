package com.hlc.lib_base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity(@LayoutRes layoutResId: Int) : AppCompatActivity(layoutResId) {

    private var loadingDialog: Dialog? = null

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

    protected fun showLoading(message: String = getString(R.string.loading)) {
        if (isFinishing || isDestroyed) return
        if (loadingDialog?.isShowing == true) return
        
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null, false)
        view.findViewById<TextView>(R.id.tv_loading_message).text = message
        loadingDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
            .apply { 
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                show() 
            }
    }

    protected fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    protected fun showError(message: String) {
        if (isFinishing || isDestroyed) return
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.dialog_confirm), null)
            .show()
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
