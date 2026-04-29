package com.hlc.mywallet.feature.home

import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityMainBinding
import com.hlc.lib_base.BaseVbActivity
import com.hlc.mywallet.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseVbActivity<ActivityHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    override fun initView() {
        binding.btnRequest.setOnClickListener {
            viewModel.loadDemo()
        }
    }

    override fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        HomeUiState.Idle -> {
                            hideLoading()
                            binding.tvStatus.text = getString(R.string.home_idle)
                        }
                        HomeUiState.Loading -> {
                            showLoading()
                            binding.tvStatus.text = getString(R.string.home_loading)
                        }
                        is HomeUiState.Success -> {
                            hideLoading()
                            binding.tvStatus.text = "${state.data.title}\n${state.data.subtitle}"
                        }
                        is HomeUiState.Error -> {
                            hideLoading()
                            binding.tvStatus.text = state.message
                            showError(state.message)
                        }
                    }
                }
            }
        }
    }
}
