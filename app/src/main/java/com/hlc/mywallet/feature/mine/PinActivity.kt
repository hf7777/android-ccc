package com.hlc.mywallet.feature.mine

import android.os.CountDownTimer
import androidx.activity.viewModels
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.enableWhen
import com.hlc.lib_base.extension.enableWhenAllFilled
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showConfirmDialog
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.BindCodeResp
import com.hlc.mywallet.data.model.resp.CheckBindingResp
import com.hlc.mywallet.databinding.ActivityBindTgBinding
import com.hlc.mywallet.databinding.ActivityPinBinding
import com.hlc.mywallet.feature.main.MainViewModel
import com.hlc.mywallet.feature.wallet.WalletViewModel
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinActivity : BaseVbActivity<ActivityPinBinding>() {

    private val mainViewModel: MainViewModel by viewModels()
    private var sendCountDownTimer: CountDownTimer? = null
    private var isSendCoolingDown = false


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
        binding.apply {
            btnSend.onClick {
                startSendCountDown()
                mainViewModel.sendPinOtp()
            }
            btnConfirm.enableWhenAllFilled(
                etPin,
                etConfirmPin,
                etTgOtp
            )
            btnSend.enableWhen(etPin,etConfirmPin) {
                val pin = etPin.text.toString().trim()
                val confirmPin = etConfirmPin.text.toString().trim()
                !isSendCoolingDown &&
                    pin.isNotEmpty() &&
                    confirmPin.isNotEmpty() &&
                    pin.length == 6 &&
                    confirmPin.length == 6 &&
                    pin == confirmPin
            }
            btnConfirm.onClick {
                val pin = binding.etPin.text.toString().trim()
                val confirmPin = binding.etConfirmPin.text.toString().trim()
                val otp = binding.etTgOtp.text.toString().trim()
                mainViewModel.setPin(pin, confirmPin, otp)
            }
        }
    }

    override fun observeData() {
        mainViewModel.sendPinOtpState.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                showLoading()
            },
            onSuccess = { _ ->
                Toaster.show(getString(R.string.sent_successfully))
                hideLoading()
            },
            onError = {
                hideLoading()
            }
        )

        mainViewModel.setPinState.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                showLoading()
            },
            onSuccess = { _ ->
                hideLoading()
                Toaster.show(StringUtils.getString(R.string.pin_set_successfully))
                finish()
            },
            onError = {
                hideLoading()
            }
        )

    }


    override fun useBaseTitleBar(): Boolean = true

    override fun getBaseTitleBarTitle(): String = StringUtils.getString(R.string.pin)

    override fun onDestroy() {
        sendCountDownTimer?.cancel()
        sendCountDownTimer = null
        super.onDestroy()
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
                isSendCoolingDown = false
                binding.btnSend.text = getString(R.string.send)
                val pin = binding.etPin.text.toString().trim()
                val confirmPin = binding.etConfirmPin.text.toString().trim()
                binding.btnSend.isEnabled =
                    pin.isNotEmpty() && confirmPin.isNotEmpty() && pin.length == 6 && confirmPin.length == 6 && pin == confirmPin
            }
        }.start()
    }

    companion object {
        private const val SEND_COUNT_DOWN_MILLIS = 10_000L
        private const val COUNT_DOWN_INTERVAL_MILLIS = 1_000L
    }
}
