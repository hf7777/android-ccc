package com.hlc.lib_base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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
            val vbClass = findViewBindingClass(javaClass)
                ?: throw IllegalStateException("ViewBinding class not found")
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

    private fun findViewBindingClass(clazz: Class<*>): Class<out ViewBinding>? {
        var type: Type? = clazz.genericSuperclass
        while (type != null) {
            when (type) {
                is ParameterizedType -> {
                    val bindingClass = extractViewBindingClass(type.actualTypeArguments.firstOrNull())
                    if (bindingClass != null) {
                        return bindingClass
                    }
                    type = (type.rawType as? Class<*>)?.genericSuperclass
                }
                is Class<*> -> {
                    type = type.genericSuperclass
                }
                else -> {
                    type = null
                }
            }
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractViewBindingClass(type: Type?): Class<out ViewBinding>? {
        val clazz = when (type) {
            is Class<*> -> type
            is ParameterizedType -> type.rawType as? Class<*>
            else -> null
        } ?: return null

        return if (ViewBinding::class.java.isAssignableFrom(clazz)) {
            clazz as Class<out ViewBinding>
        } else {
            null
        }
    }
}
