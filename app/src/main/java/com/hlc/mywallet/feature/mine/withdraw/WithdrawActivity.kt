package com.hlc.mywallet.feature.mine.withdraw

import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.extension.setUnderlineEnabled
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.data.model.resp.BankCard
import com.hlc.mywallet.data.model.resp.WithdrawStatus
import com.hlc.mywallet.databinding.ActivityWithdrawBinding
import com.hlc.mywallet.dialog.BankInfoDialog
import com.hlc.mywallet.dialog.WithdrawPinOtpBottomSheet
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.math.BigDecimal

@AndroidEntryPoint
class WithdrawActivity : BaseVbActivity<ActivityWithdrawBinding>() {

    private val viewModel: WithdrawViewModel by viewModels()
    private var withdrawStatus: WithdrawStatus? = null
    private var availableBalance = BigDecimal.ZERO
    private var adjustingAmountInput = false
    private var autoBuyCountdownJob: Job? = null
    private var hasWithdrawStatusLoaded = false

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarColorInt(ColorUtils.getColor(R.color.white))
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }

    override fun initView() {
        binding.tvRecord.setUnderlineEnabled()
        binding.btnAddBank.onClick {
            showBankInfoDialog(bankCard = null)
        }
        binding.btnSubmit.onClick {
            val amount = binding.etAmount.text?.toString()?.trim().orEmpty()
            if (amount.isEmpty()) {
                Toaster.show(getString(R.string.withdraw_amount_required))
                return@onClick
            }
            showWithdrawPinOtpSheet(amount)
        }
        binding.tvRecord.onClick {
            navigation(Routes.WITHDRAW_RECORDS)
        }
        binding.ivEdit.onClick {
            val bankCard = withdrawStatus?.bankCard ?: return@onClick
            showBankInfoDialog(bankCard = bankCard)
        }
        binding.btnAutoTrading.onClick {
            val status = withdrawStatus?.autoPayoutStatus
            viewModel.toggleAutoTrading(status)
        }
        binding.etAmount.addTextChangedListener(amountLimitWatcher)
    }

    override fun initData() {
        showPageLoading()
        viewModel.getWithdrawStatus()
    }

    override fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.withdrawStatusFlow.collect { result ->
                        when (result) {
                            is ApiResult.Loading -> {
                                if (!hasWithdrawStatusLoaded) {
                                    showPageLoading()
                                }
                            }
                            is ApiResult.Success -> {
                                hasWithdrawStatusLoaded = true
                                showPageContent()
                                renderWithdrawStatus(result.data)
                            }
                            is ApiResult.Error -> {
                                showPageError(
                                    message = result.exception.message.orEmpty(),
                                    onActionClick = { viewModel.getWithdrawStatus() }
                                )
                            }
                            else -> Unit
                        }
                    }
                }
                launch {
                    viewModel.submitWithdrawFlow.collect { result ->
                        when (result) {
                            is ApiResult.Loading -> showLoading()
                            is ApiResult.Success -> {
                                hideLoading()
                                Toaster.show(getString(R.string.submit_successfully))
                                binding.etAmount.setText("")
                                AppEventBus.post(AppEvent.MineRefreshRequested)
                                viewModel.getWithdrawStatus()
                            }
                            is ApiResult.Error -> {
                                hideLoading()
                                Toaster.show(result.exception.message.orEmpty())
                            }
                            else -> Unit
                        }
                    }
                }
                launch {
                    viewModel.bankCardFlow.collect { result ->
                        when (result) {
                            is ApiResult.Loading -> showLoading()
                            is ApiResult.Success -> {
                                hideLoading()
                                dismissBankInfoDialog()
                                Toaster.show(getString(R.string.save_successfully))
                                viewModel.getWithdrawStatus()
                            }
                            is ApiResult.Error -> {
                                hideLoading()
                                Toaster.show(result.exception.message.orEmpty())
                            }
                            else -> Unit
                        }
                    }
                }
                launch {
                    viewModel.autoTradingFlow.collect { result ->
                        when (result) {
                            is ApiResult.Loading -> showLoading()
                            is ApiResult.Success -> {
                                hideLoading()
                                viewModel.getWithdrawStatus()
                            }
                            is ApiResult.Error -> {
                                hideLoading()
                                Toaster.show(result.exception.message.orEmpty())
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun showBankInfoDialog(bankCard: BankCard?) {
        BankInfoDialog.newInstance(bankCard)
            .setOnSaveListener { req, isEdit ->
                if (isEdit) {
                    viewModel.editBankCard(req)
                } else {
                    viewModel.addBankCard(req)
                }
            }
            .show(supportFragmentManager, BankInfoDialog::class.java.simpleName)
    }

    private fun dismissBankInfoDialog() {
        val dialog = supportFragmentManager
            .findFragmentByTag(BankInfoDialog::class.java.simpleName) as? BankInfoDialog
        dialog?.dismissAllowingStateLoss()
    }

    private fun showWithdrawPinOtpSheet(amount: String) {
        WithdrawPinOtpBottomSheet.newInstance()
            .setOnVerifiedListener { pin, otp ->
                viewModel.submitWithdraw(amount, pin, otp)
            }
            .show(supportFragmentManager, WithdrawPinOtpBottomSheet.TAG)
    }

    private fun renderWithdrawStatus(status: WithdrawStatus) {
        withdrawStatus = status
        availableBalance = status.availableBalance.toBigDecimalOrZero()

        binding.btnAddBank.visibleOrGone(status.bankCard == null)
        binding.clBankInfo.visibleOrGone(status.bankCard != null)

        binding.tvAccountName.text = status.bankCard?.accountName.orEmpty()
        binding.tvAccountNumber.text = status.bankCard?.accountNo.orEmpty()
        binding.tvBene.text = status.bankCard?.beneName.orEmpty()
        binding.tvIfsc.text = status.bankCard?.ifsc.orEmpty()
        binding.tvBalance.text = "${StringUtils.getString(R.string.price_symbol)}${status.availableBalance.orEmpty()}"
        binding.tvUsed.text = "${status.currentWithdrawTimes}/${status.maxWithdrawTimes} used"
        binding.tvRemainingCount.text = "${status.maxWithdrawTimes - status.currentWithdrawTimes}"
        binding.tvMinAmount.text = "Minimum amount: ₹${status.minWithdrawAmount.orEmpty()}"

        val autoEnabled = status.autoPayoutStatus.equals(AUTO_STATUS_ENABLE, ignoreCase = true)
        binding.btnAutoTrading.text = if (autoEnabled) {
            getString(R.string.stop_auto_trade)
        } else {
            getString(R.string.restart_auto_trading)
        }
        binding.btnAutoTrading.isEnabled = autoEnabled
        binding.tvTradingStatus.text = if (autoEnabled) {
            getString(R.string.auto_trading_enabled)
        } else {
            getString(R.string.auto_trading_stopped)
        }

        binding.dot.shapeDrawableBuilder.setSolidColor(
            if (autoEnabled) ColorUtils.getColor(R.color.status_green) else ColorUtils.getColor(R.color.theme)
        ).intoBackground()

        setupAutoTradingTimeUi(status, autoEnabled)

        clampAmountToAvailableBalance()
    }

    private fun setupAutoTradingTimeUi(status: WithdrawStatus, autoEnabled: Boolean) {
        stopAutoBuyCountdown()
        binding.tvTradingTime.visible()
        if (autoEnabled) {
            binding.tvTradingTime.text = status.description.orEmpty()
            return
        }

        val remainingSeconds = computeAutoBuyRemainingSeconds(status)
        if (remainingSeconds > 0L) {
            binding.btnAutoTrading.isEnabled = false
            binding.tvTradingTime.text = formatCanEnableIn(remainingSeconds)
            startAutoBuyCountdown(status, remainingSeconds)
        } else {
            // 自动代付关闭且可立即开启时，按约定展示空字符串
            binding.btnAutoTrading.isEnabled = true
            binding.tvTradingTime.gone()
        }
    }

    private fun stopAutoBuyCountdown() {
        autoBuyCountdownJob?.cancel()
        autoBuyCountdownJob = null
    }

    private fun computeAutoBuyRemainingSeconds(status: WithdrawStatus): Long {
        return (status.autoBuyRemainingSeconds ?: 0L).coerceAtLeast(0L)
    }

    private fun formatCanEnableIn(remainingSeconds: Long): String {
        val hours = remainingSeconds / 3600L
        val minutes = (remainingSeconds % 3600L) / 60L
        return "Can restart in ${hours}h ${minutes}m"
    }

    private fun startAutoBuyCountdown(status: WithdrawStatus, initialRemainingSeconds: Long) {
        autoBuyCountdownJob = lifecycleScope.launch {
            var remaining = initialRemainingSeconds
            while (isActive && remaining > 0L) {
                binding.tvTradingTime.text = formatCanEnableIn(remaining)
                delay(1000L)
                remaining--
            }

            binding.tvTradingTime.text = ""
            binding.btnAutoTrading.isEnabled = true
            binding.btnAutoTrading.isSelected = false
        }
    }

    private fun clampAmountToAvailableBalance() {
        val amountText = binding.etAmount.text?.toString()?.trim().orEmpty()
        val amount = amountText.toBigDecimalOrNull() ?: return
        if (amount > availableBalance) {
            val maxText = availableBalance.toPlainString()
            binding.etAmount.setText(maxText)
            binding.etAmount.setSelection(maxText.length)
        }
    }

    private val amountLimitWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(s: Editable?) {
            if (adjustingAmountInput) return
            val input = s?.toString()?.trim().orEmpty()
            if (input.isEmpty()) return
            val value = input.toBigDecimalOrNull() ?: return
            if (value <= availableBalance) return
            adjustingAmountInput = true
            val maxText = availableBalance.toPlainString()
            binding.etAmount.setText(maxText)
            binding.etAmount.setSelection(maxText.length)
            adjustingAmountInput = false
        }
    }

    override fun onDestroy() {
        stopAutoBuyCountdown()
        super.onDestroy()
    }

    override fun useBaseTitleBar(): Boolean = true

    override fun getBaseTitleBarTitle(): String = getString(R.string.withdraw)

    companion object {
        private const val AUTO_STATUS_ENABLE = "enable"
    }
}

private fun String?.toBigDecimalOrZero(): BigDecimal {
    return this?.toBigDecimalOrNull() ?: BigDecimal.ZERO
}