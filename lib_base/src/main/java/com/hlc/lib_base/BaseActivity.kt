package com.hlc.lib_base

import android.content.res.Configuration
import android.os.Bundle
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.Toaster
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.PageStateLayout
import com.hlc.lib_base.widget.TitleBar
import com.hlc.lib_base.widget.showLoading
import com.hlc.lib_base.autosize.AutoSizeFoldConfigurator
import com.hlc.lib_base.autosize.FoldScreenUtil
import com.hlc.lib_base.router.Router
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig

abstract class BaseActivity(@LayoutRes layoutResId: Int) : AppCompatActivity(layoutResId) {

    private var pageStateLayout: PageStateLayout? = null
    private var pageContentContainer: FrameLayout? = null
    private var baseTitleBar: TitleBar? = null
    private var hasAppliedActivityTransition = false

    override fun onCreate(savedInstanceState: Bundle?) {
        applyActivityTransitions()
        super.onCreate(savedInstanceState)
        attachBaseTitleBarIfNeeded()
        attachPageStateLayout()
        initImmersionBar()
        initView()
        initData()
        observeData()
    }

    protected fun applyActivityTransitions() {
        if (hasAppliedActivityTransition) return
        hasAppliedActivityTransition = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return

        val enterAnim = intent.getIntExtra(Router.EXTRA_ENTER_ANIM, 0)
        val exitAnim = intent.getIntExtra(Router.EXTRA_EXIT_ANIM, 0)
        if (enterAnim != 0 && exitAnim != 0) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim)
        }
        overrideActivityTransition(
            OVERRIDE_TRANSITION_CLOSE,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }

    override fun onStart() {
        super.onStart()
        refreshFoldableAutoSize()
    }

    override fun onDestroy() {
        hideLoading()
        pageStateLayout = null
        pageContentContainer = null
        baseTitleBar = null
        super.onDestroy()
    }

    /**
     * 仅在生命周期中做屏幕适配。勿在 [getResources] 中反复改 density，否则易导致输入法无法弹出。
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        refreshFoldableAutoSize()
    }

    private fun refreshFoldableAutoSize() {
        if (AutoSizeConfig.getInstance().isStop) return
        if (!FoldScreenUtil.isWindowManagerReady(this)) return
        AutoSizeFoldConfigurator.configure(this, resources.configuration)
        AutoSize.autoConvertDensityOfGlobal(this)
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

    protected open fun useBaseTitleBar(): Boolean = false

    protected open fun getBaseTitleBarTitle(): String? = null

    protected open fun isBaseTitleBarBackVisible(): Boolean = true

    @ColorInt
    protected open fun getBaseTitleBarBackgroundColor(): Int = getColor(R.color.theme)

    @ColorInt
    protected open fun getBaseTitleBarTitleColor(): Int = getColor(android.R.color.white)

    protected fun getBaseTitleBar(): TitleBar? = baseTitleBar

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

    private fun attachBaseTitleBarIfNeeded() {
        if (!useBaseTitleBar()) return

        val contentParent = findViewById<ViewGroup>(android.R.id.content) ?: return
        val contentView = contentParent.getChildAt(0) ?: return
        if (contentView === pageContentContainer) return

        contentParent.removeView(contentView)

        val rootLayout = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
        }

        val titleBar = TitleBar(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(getBaseTitleBarBackgroundColor())
            setTitleColor(getBaseTitleBarTitleColor())
            setTitle(getBaseTitleBarTitle())
            setBackVisible(isBaseTitleBarBackVisible())
            setOnBackClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        val contentContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            addView(
                contentView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        rootLayout.addView(titleBar)
        rootLayout.addView(contentContainer)
        contentParent.addView(rootLayout)

        baseTitleBar = titleBar
        pageContentContainer = contentContainer
    }

    private fun attachPageStateLayout() {
        val rootView = pageContentContainer
            ?: findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0) as? ViewGroup
            ?: return
        val layout = PageStateLayout(this)
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
