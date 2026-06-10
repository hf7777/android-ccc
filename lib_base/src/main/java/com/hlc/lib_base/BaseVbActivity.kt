package com.hlc.lib_base

import android.os.Bundle
import android.os.Build
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

abstract class BaseVbActivity<VB : ViewBinding> : BaseActivity(0) {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        applyActivityTransitions()
        try {
            val vbClass = resolveViewBindingClass<VB>()
            val method = vbClass.getMethod("inflate", LayoutInflater::class.java)
            _binding = method.invoke(null, layoutInflater) as VB
            setContentView(binding.root)
        } catch (e: Exception) {
            throw RuntimeException(getString(R.string.error_viewbinding_init, e.message), e)
        }

        super.onCreate(savedInstanceState)
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
