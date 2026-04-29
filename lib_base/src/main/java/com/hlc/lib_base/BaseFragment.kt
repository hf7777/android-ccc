package com.hlc.lib_base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    private var loadingDialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImmersionBar()
        initView()
        initData()
        observeData()
    }

    override fun onDestroyView() {
        hideLoading()
        super.onDestroyView()
    }

    /**
     * 初始化沉浸式状态栏，子类可重写自定义
     * 默认不处理，由 Activity 统一管理
     */
    protected open fun initImmersionBar() = Unit

    protected open fun initView() = Unit

    protected open fun initData() = Unit

    protected open fun observeData() = Unit

    protected fun showLoading(message: String = getString(R.string.loading)) {
        if (!isAdded || isDetached) return
        if (loadingDialog?.isShowing == true) return
        
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_loading, null, false)
        view.findViewById<TextView>(R.id.tv_loading_message).text = message
        loadingDialog = AlertDialog.Builder(requireContext())
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
        if (!isAdded || isDetached) return
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.dialog_confirm), null)
            .show()
    }

    /**
     * Fragment 需要独立控制状态栏时使用
     */
    protected fun setFragmentStatusBar() {
        ImmersionBarHelper.initDefault(this)
    }

    /**
     * Fragment 设置透明状态栏
     */
    protected fun setTransparentStatusBar(darkFont: Boolean = true) {
        ImmersionBarHelper.initTransparent(this, darkFont)
    }
}
