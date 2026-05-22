package com.hlc.mywallet.feature.wallet

import android.os.CountDownTimer
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.StringUtils
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.enableWhen
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.databinding.FragmentPayChannelSetup2Binding
import com.hlc.mywallet.extension.setupPasswordVisibilityToggle
import com.hlc.mywallet.feature.main.MainViewModel
import com.hlc.mywallet.feature.wallet.bean.PayChannelSetupArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * @author Wade
 * @since 2026/5/13
 */
@AndroidEntryPoint
class PayChannelSetup2Fragment: BaseVbFragment<FragmentPayChannelSetup2Binding>() {

    private var setupArgs: PayChannelSetupArgs? = null
    private var sendCountDownTimer: CountDownTimer? = null

    private val viewModel: MainViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArgs = arguments?.getParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS)
    }

    override fun initView() {
        binding.apply {
            etPhone.text = setupArgs?.phone.orEmpty()

            btnNext.enableWhen( etOtp) {
                val otp = etOtp.text.toString().trim()
                otp.isNotEmpty()
            }

            btnSend.onClick {
                setupArgs?.let { args ->
                    viewModel.sendTgOtp(args.phone, args.channelCode)
                }
            }

            btnNext.onClick {
                applyAndLogin()
            }
            etOtp.setupPasswordVisibilityToggle()
        }
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
    }

    override fun onDestroyView() {
        sendCountDownTimer?.cancel()
        sendCountDownTimer = null
        super.onDestroyView()
    }

    private fun startSendCountDown() {
        sendCountDownTimer?.cancel()
        binding.btnSend.isEnabled = false
        sendCountDownTimer = object : CountDownTimer(SEND_COUNT_DOWN_MILLIS, COUNT_DOWN_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000L
                binding.btnSend.text = "${seconds}s"
            }

            override fun onFinish() {
                binding.btnSend.text = getString(R.string.send)
                binding.btnSend.isEnabled = true
            }
        }.start()
    }

    companion object {
        private const val SEND_COUNT_DOWN_MILLIS = 60_000L
        private const val COUNT_DOWN_INTERVAL_MILLIS = 1_000L

        fun newInstance(setupArgs: PayChannelSetupArgs?) = PayChannelSetup2Fragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS, setupArgs)
            }
        }
    }
}
