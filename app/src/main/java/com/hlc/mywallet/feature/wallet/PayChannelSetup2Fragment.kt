package com.hlc.mywallet.feature.wallet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.StringUtils
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.enableWhen
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showConfirmDialog
import com.hlc.mywallet.R
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.databinding.FragmentPayChannelSetup2Binding
import com.hlc.mywallet.dialog.InstallGuideDialog
import com.hlc.mywallet.extension.setupPasswordVisibilityToggle
import com.hlc.mywallet.feature.main.MainViewModel
import com.hlc.mywallet.feature.wallet.bean.PayChannelSetupArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * @author Wade
 * @since 2026/5/13
 */
@AndroidEntryPoint
class PayChannelSetup2Fragment: BaseVbFragment<FragmentPayChannelSetup2Binding>() {

    private var setupArgs: PayChannelSetupArgs? = null
    private var sendCountDownTimer: CountDownTimer? = null
    private var activeStatusPollJob: Job? = null
    private var isOtpActive = false

    private var isAutoBuy = false

    private val viewModel: MainViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArgs = arguments?.getParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS)
    }

    override fun initView() {
        binding.apply {

            isAutoBuy = setupArgs?.isAutoBuy == true
            //目前只有这一个钱包需要 ACTIVE OTP
            groupAutoBtn.visibleOrGone(isAutoBuy && ACTIVE_OTP_WALLETS.contains(setupArgs?.channelCode))
            btnSend.isEnabled = !isAutoBuy

            tvTips.text = setupArgs?.description.orEmpty()
            etPhone.text = setupArgs?.phone.orEmpty()

            btnGuide.onClick {
                val channelCode = setupArgs?.channelCode.orEmpty()
                if (channelCode.isEmpty()) {
                    return@onClick
                }
                walletViewModel.moduleGuideList(channelCode)
            }

            btnActiveOtp.onClick {
                handleActiveOtpClick()
            }

            btnNext.enableWhen( etOtp) {
                val otp = etOtp.text.toString().trim()
                otp.isNotEmpty()
            }

            btnSend.onClick {
                setupArgs?.let { args ->
                    viewModel.sendTgOtp(args.phone, args.channelCode)
                }
            }
            btnSend.isEnabled = false

            btnNext.onClick {
                applyAndLogin()
            }
            etOtp.setupPasswordVisibilityToggle()
        }
    }

    override fun onResume() {
        super.onResume()
        startAutoActiveStatusPolling()
    }

    override fun onPause() {
        stopAutoActiveStatusPolling()
        super.onPause()
    }


    /**
     * 先登录，再轮询授权状态
     */
    private fun applyAndLogin() {
        setupArgs?.let { args ->
            if (args.phone.isNotEmpty()) {
                val otp = binding.etOtp.text.toString().trim()
                walletViewModel.loginAndGetPermissionStatus(args.phone, args.channelCode, otp)
            }
        }
    }

    override fun observeData() {
        viewModel.sendTgOtpState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = {
                hideLoading()
                Toaster.show(R.string.sent_successfully)
                startSendCountDown()
            },
            onError = {
                hideLoading()
            }
        )

        walletViewModel.applyPermissionFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading(StringUtils.getString(R.string.logging_in))
            },
            onSuccess = { _ -> },
            onError = {
                hideLoading()
            }
        )

        walletViewModel.loginWalletFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {},
            onSuccess = {
                hideLoading()
                val stepArgs = setupArgs?.copy(
                    otp = binding.etOtp.text.toString().trim()
                ) ?: return@collectWithError
                (activity as? PayChannelSetupActivity)?.navigateToStep3(stepArgs)
            },
            onError = {
                hideLoading()
            }
        )

        walletViewModel.moduleGuideListFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = { guides ->
                hideLoading()
                if (guides.isEmpty()) {
                    Toaster.show(R.string.error_data_empty)
                    return@collectWithError
                }
                InstallGuideDialog.newInstance(guides)
                    .show(childFragmentManager, InstallGuideDialog::class.java.simpleName)
            },
            onError = {
                hideLoading()
            }
        )

        walletViewModel.autoActiveStatusFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {},
            onSuccess = { status ->
                updateActiveOtpButtonText(status.active)
            },
            onError = {
                updateActiveOtpButtonText(false)
            }
        )
    }

    override fun onDestroyView() {
        stopAutoActiveStatusPolling()
        sendCountDownTimer?.cancel()
        sendCountDownTimer = null
        super.onDestroyView()
    }

    private fun startSendCountDown() {
        sendCountDownTimer?.cancel()
        updateSendButtonEnableState()
        sendCountDownTimer = object : CountDownTimer(SEND_COUNT_DOWN_MILLIS, COUNT_DOWN_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000L
                binding.btnSend.text = "${seconds}s"
            }

            override fun onFinish() {
                sendCountDownTimer = null
                binding.btnSend.text = getString(R.string.send)
                updateSendButtonEnableState()
            }
        }.start()
    }

    private fun handleActiveOtpClick() {
        val launchIntent = requireContext().packageManager.getLaunchIntentForPackage(AA_OTP_PACKAGE_NAME)
        if (launchIntent != null) {
            startActivity(launchIntent)
            return
        }
        showConfirmDialog(
            content = getString(R.string.install_aa_otp_first),
            confirmText = getString(R.string.download_now)
        ) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(AA_OTP_DOWNLOAD_URL))
            startActivity(browserIntent)
        }
    }

    private fun startAutoActiveStatusPolling() {
        if (activeStatusPollJob?.isActive == true) return
        activeStatusPollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                val args = setupArgs
                if (args != null && args.phone.isNotEmpty() && args.channelCode.isNotEmpty()) {
                    walletViewModel.autoActiveStatus(args.phone, args.channelCode)
                } else {
                    updateActiveOtpButtonText(false)
                }
                delay(AUTO_ACTIVE_STATUS_POLL_INTERVAL_MILLIS)
            }
        }
    }

    private fun stopAutoActiveStatusPolling() {
        activeStatusPollJob?.cancel()
        activeStatusPollJob = null
    }

    private fun updateActiveOtpButtonText(active: Boolean) {
        isOtpActive = active
        updateSendButtonEnableState()
        if (active) {
            binding.btnActiveOtp.text = getString(R.string.active_otp_running)
            return
        }
        val context = context ?: return
        val launchIntent = context.packageManager.getLaunchIntentForPackage(AA_OTP_PACKAGE_NAME)
        binding.btnActiveOtp.text = if (launchIntent == null) {
            getString(R.string.active_otp_not_installed)
        } else {
            getString(R.string.active_otp_not_running)
        }
    }

    private fun updateSendButtonEnableState() {
        if (isAutoBuy) {
            binding.btnSend.isEnabled = isOtpActive && sendCountDownTimer == null
        } else {
            binding.btnSend.isEnabled = sendCountDownTimer == null
        }
    }

    companion object {

        private const val SEND_COUNT_DOWN_MILLIS = 60_000L
        private const val COUNT_DOWN_INTERVAL_MILLIS = 1_000L
        private const val AUTO_ACTIVE_STATUS_POLL_INTERVAL_MILLIS = 1_000L
        private const val AA_OTP_PACKAGE_NAME = "com.s.autoactiveotpaa"
        private const val AA_OTP_DOWNLOAD_URL = "https://api-public-nn.s3.ap-south-1.amazonaws.com/apk/aaotp.apk"

        private const val MBK_POCKET = "mobikwik_pocket"

        private val ACTIVE_OTP_WALLETS = arrayOf("mobikwik_pocket", "mobikwik_upi")

        fun newInstance(setupArgs: PayChannelSetupArgs?) = PayChannelSetup2Fragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS, setupArgs)
            }
        }
    }
}
