package com.hlc.lib_base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hjq.toast.Toaster
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.PageStateLayout
import com.hlc.lib_base.widget.showLoading

abstract class BaseFragment : Fragment() {

    private var pageStateLayout: PageStateLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachPageStateLayout(view)
        initImmersionBar()
        initView()
        initData()
        observeData()
    }

    override fun onDestroyView() {
        hideLoading()
        pageStateLayout = null
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

    protected fun showPageLoading(message: String = getString(R.string.loading)) {
        pageStateLayout?.showLoading(message)
    }

    protected fun showPageEmpty(
        message: String = getString(R.string.page_empty),
        actionText: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        pageStateLayout?.showEmpty(message, actionText, onActionClick)
    }

    protected fun showPageError(
        message: String = getString(R.string.page_error),
        actionText: String = getString(R.string.page_retry),
        onActionClick: (() -> Unit)? = null
    ) {
        pageStateLayout?.showError(message, actionText, onActionClick)
    }

    protected fun showPageContent() {
        pageStateLayout?.showContent()
    }

    protected fun isPageStateShowing(): Boolean {
        return pageStateLayout?.isShowingState() == true
    }

    protected fun showLoading(message: String = getString(R.string.loading)) {
        showLoading(message, false)
    }

    protected fun showError(message: String) {
        if (!isAdded || isDetached) return
        Toaster.show(message)
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

    private fun attachPageStateLayout(view: View) {
        val rootView = view as? ViewGroup ?: return
        val layout = PageStateLayout(requireContext())
        rootView.addView(
            layout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        pageStateLayout = layout
    }
}
