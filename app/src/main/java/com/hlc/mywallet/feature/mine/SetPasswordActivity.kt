package com.hlc.mywallet.feature.mine

import android.content.Intent
import android.os.CountDownTimer
import androidx.activity.viewModels
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.enableWhen
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.getRouterBoolean
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.SliderCaptchaResp
import com.hlc.mywallet.databinding.ActivitySetPasswordBinding
import com.hlc.mywallet.dialog.SliderCaptchaDialog
import com.hlc.mywallet.extension.setupPasswordVisibilityToggle
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetPasswordActivity : BaseVbActivity<ActivitySetPasswordBinding>() {

    private val viewModel: SetPasswordViewModel by viewModels()
    private var sendCountDownTimer: CountDownTimer? = null
    private var isSendCoolingDown = false
    private var hasSentOtp = false
    private var isSendingPasswordOtp = false
    private var sliderCaptchaDialog: SliderCaptchaDialog? = null

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
        if (intent.getRouterBoolean(Constants.RouterKeys.SHOW_SIGN)) {
            binding.groupSign.visible()
        } else {
            binding.groupSign.gone()
        }
        binding.apply {
            btnSend.enableWhen(etPhone, etNewPassword, etRepeatPassword) {
                canSendOtp()
            }
            btnConfirm.enableWhen(etPhone, etNewPassword, etRepeatPassword, etOtp) {
                canResetPassword()
            }
            btnSend.onClick {
                if (validateBeforeSendOtp()) {
                    viewModel.sliderCaptcha()
                }
            }
            btnConfirm.onClick {
                if (validateBeforeReset()) {
                    viewModel.resetPassword(
                        phone = etPhone.text.toString().trim(),
                        password = etNewPassword.text.toString().trim(),
                        confirmPassword = etRepeatPassword.text.toString().trim(),
                        otp = etOtp.text.toString().trim()
                    )
                }
            }
            tvSignIn.onClick {
                finish()
            }
            etNewPassword.setupPasswordVisibilityToggle()
            etRepeatPassword.setupPasswordVisibilityToggle()
        }
    }

    override fun observeData() {
        viewModel.sliderCaptchaState.collectWithError(
            lifecycleOwner = this,
            onLoading = { showLoading() },
            onSuccess = { captcha ->
                hideLoading()
                showOrUpdateSliderCaptcha(captcha)
            },
            onError = { hideLoading() }
        )

        viewModel.sendPasswordOtpState.collectWithError(
            lifecycleOwner = this,
            onLoading = { showLoading() },
            onSuccess = {
                hideLoading()
                isSendingPasswordOtp = false
                hasSentOtp = true
                Toaster.show(getString(R.string.sent_successfully))
                startSendCountDown()
            },
            onError = {
                hideLoading()
                isSendingPasswordOtp = false
            }
        )

        viewModel.resetPasswordState.collectWithError(
            lifecycleOwner = this,
            onLoading = { showLoading() },
            onSuccess = {
                Toaster.show(getString(R.string.password_reset_successfully))
                viewModel.logout()
            },
            onError = { hideLoading() }
        )

        viewModel.logoutState.collectWithError(
            lifecycleOwner = this,
            onLoading = { showLoading() },
            onSuccess = {
                hideLoading()
                Router.navigation(Routes.LOGIN)
                    .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .navigation(this)
            },
            onError = { hideLoading() }
        )
    }

    private fun showOrUpdateSliderCaptcha(captcha: SliderCaptchaResp) {
        val showingDialog = sliderCaptchaDialog?.takeIf { it.isAdded }
        if (showingDialog != null) {
            showingDialog.updateCaptcha(captcha)
            return
        }

        sliderCaptchaDialog = SliderCaptchaDialog
            .newInstance(captcha)
            .setOnSuccessListener { captchaId, sliderPosition ->
                if (isSendingPasswordOtp) {
                    return@setOnSuccessListener
                }
                isSendingPasswordOtp = true
                sliderCaptchaDialog?.dismiss()
                viewModel.sendPasswordOtp(
                    phone = binding.etPhone.text.toString().trim(),
                    captchaId = captchaId,
                    sliderPosition = sliderPosition
                )
            }
            .setOnRefreshListener {
                viewModel.sliderCaptcha()
            }
        sliderCaptchaDialog?.show(supportFragmentManager, "SliderCaptchaDialog")
    }

    private fun validateBeforeSendOtp(): Boolean {
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etNewPassword.text.toString().trim()
        val repeatPassword = binding.etRepeatPassword.text.toString().trim()
        return when {
            phone.length < MIN_PHONE_LENGTH -> {
                Toaster.show(getString(R.string.please_enter_a_valid_phone_number))
                false
            }
            password.length < MIN_PASSWORD_LENGTH -> {
                Toaster.show(getString(R.string.please_enter_your_password))
                false
            }
            password != repeatPassword -> {
                Toaster.show(getString(R.string.passwords_do_not_match))
                false
            }
            else -> true
        }
    }

    private fun validateBeforeReset(): Boolean {
        if (!hasSentOtp) {
            Toaster.show(getString(R.string.please_enter_your_otp))
            return false
        }
        if (!validateBeforeSendOtp()) {
            return false
        }
        if (binding.etOtp.text.toString().trim().isEmpty()) {
            Toaster.show(getString(R.string.please_enter_your_otp))
            return false
        }
        return true
    }

    private fun canSendOtp(): Boolean {
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etNewPassword.text.toString().trim()
        val repeatPassword = binding.etRepeatPassword.text.toString().trim()
        return !isSendCoolingDown &&
            phone.length >= MIN_PHONE_LENGTH &&
            password.length >= MIN_PASSWORD_LENGTH &&
            repeatPassword.isNotEmpty()
    }

    private fun canResetPassword(): Boolean {
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etNewPassword.text.toString().trim()
        val repeatPassword = binding.etRepeatPassword.text.toString().trim()
        return phone.length >= MIN_PHONE_LENGTH &&
            password.length >= MIN_PASSWORD_LENGTH &&
            password == repeatPassword &&
            binding.etOtp.text.toString().trim().isNotEmpty()
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
                binding.btnSend.isEnabled = canSendOtp()
            }
        }.start()
    }

    override fun getBaseTitleBarTitle(): String = getString(R.string.reset_password)

    override fun useBaseTitleBar(): Boolean = true

    override fun onDestroy() {
        sendCountDownTimer?.cancel()
        sendCountDownTimer = null
        sliderCaptchaDialog = null
        super.onDestroy()
    }

    companion object {
        private const val SEND_COUNT_DOWN_MILLIS = 60_000L
        private const val COUNT_DOWN_INTERVAL_MILLIS = 1_000L
        private const val MIN_PHONE_LENGTH = 1
        private const val MIN_PASSWORD_LENGTH = 6
    }
}
