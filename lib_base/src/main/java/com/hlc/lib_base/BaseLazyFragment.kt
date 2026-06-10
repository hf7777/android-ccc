package com.hlc.lib_base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class BaseLazyFragment<VB : ViewBinding> : BaseFragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    private var isViewPrepared = false
    private var hasLoadedData = false

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            val vbClass = resolveViewBindingClass<VB>()
            val method = vbClass.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            _binding = method.invoke(null, inflater, container, false) as VB
            return binding.root
        } catch (e: Exception) {
            throw RuntimeException(getString(R.string.error_viewbinding_init, e.message), e)
        }
    }

    final override fun initData() {
        isViewPrepared = true
        if (enableLazyLoad()) {
            tryLazyLoad()
        } else if (!hasLoadedData) {
            dispatchLoadData()
        }
    }

    override fun onResume() {
        super.onResume()
        tryLazyLoad()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            tryLazyLoad()
        }
    }

    override fun onDestroyView() {
        isViewPrepared = false
        _binding = null
        super.onDestroyView()
    }

    protected open fun enableLazyLoad(): Boolean = true

    protected abstract fun loadLazyData()

    protected fun reloadLazyData() {
        hasLoadedData = false
        tryLazyLoad()
    }

    protected fun hasLoadedLazyData(): Boolean = hasLoadedData

    private fun tryLazyLoad() {
        if (!enableLazyLoad() || !isViewPrepared || hasLoadedData || !isFragmentVisible()) {
            return
        }
        dispatchLoadData()
    }

    private fun dispatchLoadData() {
        hasLoadedData = true
        loadLazyData()
    }

    private fun isFragmentVisible(): Boolean {
        return isResumed && !isHidden && view != null
    }
}
