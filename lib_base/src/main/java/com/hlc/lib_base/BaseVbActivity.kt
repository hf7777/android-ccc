package com.hlc.lib_base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseVbActivity<VB : ViewBinding> : BaseActivity(0) {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            val type = javaClass.genericSuperclass as ParameterizedType
            val vbClass = type.actualTypeArguments[0] as Class<VB>
            val method = vbClass.getMethod("inflate", LayoutInflater::class.java)
            _binding = method.invoke(null, layoutInflater) as VB
            setContentView(binding.root)
        } catch (e: Exception) {
            throw RuntimeException(getString(R.string.error_viewbinding_init, e.message), e)
        }
        
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
