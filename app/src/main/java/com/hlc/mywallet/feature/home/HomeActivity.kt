package com.hlc.mywallet.feature.home

import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseVbActivity<ActivityHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    override fun initView() {
        binding.btnRequest.onClick {

        }
    }

    override fun observeData() {

    }
}
