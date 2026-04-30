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
            viewModel.loadDemo()
        }
    }

    override fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ApiResult.Loading -> {
                            showLoading()
                            binding.tvStatus.text = getString(R.string.home_loading)
                        }
                        is ApiResult.Success -> {
                            hideLoading()
                            binding.tvStatus.text = "${state.data.title}\n${state.data.subtitle}"
                        }
                        is ApiResult.Error -> {
                            hideLoading()
                            binding.tvStatus.text = state.exception.message
                            showError(state.exception.message ?: getString(R.string.request_failed))
                        }
                        else -> {} // Idle 或其他状态不处理
                    }
                }
            }
        }
    }
}
