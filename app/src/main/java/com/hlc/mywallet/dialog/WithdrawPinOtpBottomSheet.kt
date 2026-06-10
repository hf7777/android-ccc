package com.hlc.mywallet.dialog

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.viewModels
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseBottomSheetDialog
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.enableWhen
import com.hlc.lib_base.extension.enableWhenAllFilled
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.DialogWithdrawPinOtpBinding
import com.hlc.mywallet.extension.setupPasswordVisibilityToggle
import com.hlc.mywallet.feature.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawPinOtpBottomSheet : BaseBottomSheetDialog<DialogWithdrawPinOtpBinding>() {

    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })

    private var sendCountDownTimer: CountDownTimer? = null
    private var isSendCoolingDown = false
    private var onVerifiedListener: ((pin: String, otp: String) -> Unit)? = null

    override fun initView() {
        binding.apply {
            ivClose.onClick { dismiss() }
            etPin.setupPasswordVisibilityToggle()
            btnSend.onClick {
                startSendCountDown()
                mainViewModel.sendWithdrawOtp()
            }
            btnConfirm.enableWhenAllFilled(etPin, etTgOtp)
            btnSend.enableWhen(etPin) {
                val pin = etPin.text.toString().trim()
                !isSendCoolingDown && pin.length == PIN_LENGTH
            }
            btnConfirm.onClick {
                val pin = etPin.text.toString().trim()
                if (pin.length != PIN_LENGTH) {
                    Toaster.show(getString(R.string._6_digits))
                    return@onClick
                }
                mainViewModel.checkPin(pin)
            }
        }
        observeData()
    }

    private fun observeData() {
        mainViewModel.sendWithdrawOtpState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = { showLoading() },
            onSuccess = {
                hideLoading()
                Toaster.show(getString(R.string.sent_successfully))
            },
            onError = {
                hideLoading()
                resetSendCountDown()
            }
        )

        mainViewModel.checkPinState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = { showLoading() },
            onSuccess = { verified ->
                hideLoading()
                if (verified) {
                    val pin = binding.etPin.text.toString().trim()
                    val otp = binding.etTgOtp.text.toString().trim()
                    onVerifiedListener?.invoke(pin, otp)
                    dismiss()
                } else {
                    Toaster.show(getString(R.string.incorrect_pin))
                }
            },
            onError = {
                hideLoading()
            }
        )
    }

    fun setOnVerifiedListener(listener: (pin: String, otp: String) -> Unit): WithdrawPinOtpBottomSheet {
        onVerifiedListener = listener
        return this
    }

    override fun onDestroyView() {
        sendCountDownTimer?.cancel()
        sendCountDownTimer = null
        super.onDestroyView()
    }

    private fun startSendCountDown() {
        sendCountDownTimer?.cancel()
        isSendCoolingDown = true
        binding.btnSend.isEnabled = false
        sendCountDownTimer = object : CountDownTimer(SEND_COUNT_DOWN_MILLIS, COUNT_DOWN_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000L
                binding.btnSend.text = "${seconds}s"
            }

            override fun onFinish() {
                resetSendCountDown()
            }
        }.start()
    }

    private fun resetSendCountDown() {
        isSendCoolingDown = false
        binding.btnSend.text = getString(R.string.send)
        val pin = binding.etPin.text.toString().trim()
        binding.btnSend.isEnabled = !isSendCoolingDown && pin.length == PIN_LENGTH
    }

    companion object {
        private const val PIN_LENGTH = 6
        private const val SEND_COUNT_DOWN_MILLIS = 10_000L
        private const val COUNT_DOWN_INTERVAL_MILLIS = 1_000L
        const val TAG = "WithdrawPinOtpBottomSheet"

        fun newInstance(): WithdrawPinOtpBottomSheet = WithdrawPinOtpBottomSheet()
    }
}
