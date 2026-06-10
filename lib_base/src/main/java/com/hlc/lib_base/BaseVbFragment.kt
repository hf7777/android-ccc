package com.hlc.lib_base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class BaseVbFragment<VB : ViewBinding> : BaseFragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
