package com.hlc.mywallet.feature.deposit

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.FragmentUsdtBinding
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class USDTFragment : BaseVbFragment<FragmentUsdtBinding>() {

    private val viewModel: DepositViewModel by activityViewModels()
    private var platUsdtRate: Double = 0.0

    override fun initView() {
        binding.etUsdt.clearFocus()
        viewModel.loadCachedPriceInfo()
        setupInputListener()
        setupClickListener()
    }

    override fun observeData() {
        observePriceInfo()
        observeUsdtPayResult()
    }

    private fun setupClickListener() {
        binding.btnNext.onClick {
            val usdtAmount = binding.etUsdt.text.toString().toIntOrNull() ?: 0
            if (usdtAmount in 100..20000) {
                viewModel.usdtPay(usdtAmount)
            } else {
                Toaster.show(R.string.the_input_range_usdt)
            }
        }
    }

    private fun observePriceInfo() {
        lifecycleScope.launch {
            viewModel.priceInfoFlow.collect { priceInfo ->
                priceInfo?.let {
                    platUsdtRate = it.platUsdtRate?.toDoubleOrNull() ?: 0.0
                    calculateDeposit()
                }
            }
        }
    }

    private fun observeUsdtPayResult() {
        viewModel.usdtPayResultFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = { showLoading() },
            onSuccess = { data ->
                hideLoading()
                data.cashierUrl?.let { url ->
                    Router.navigation(Routes.WEB)
                        .with("url", url)
                        .with("title", getString(R.string.payment))
                        .navigation(requireContext())
                } ?: Toaster.show(getString(R.string.cashier_url_is_empty))
            },
            onError = { errorMsg ->
                hideLoading()
            }
        )
    }

    private fun setupInputListener() {
        binding.etUsdt.addTextChangedListener { text ->
            calculateDeposit()
        }
    }

    private fun calculateDeposit() {
        val usdtAmount = binding.etUsdt.text.toString().toIntOrNull() ?: 0
        val depositAmount = (usdtAmount * platUsdtRate)
        binding.tvDeposit.text = depositAmount.toString().formatNumber()
    }

    companion object {
        fun newInstance() = USDTFragment()
    }
}
