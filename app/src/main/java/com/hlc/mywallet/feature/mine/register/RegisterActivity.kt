package com.hlc.mywallet.feature.mine.register

import android.os.CountDownTimer
import androidx.activity.viewModels
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.enableWhen
import com.hlc.lib_base.extension.enableWhenAllFilled
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.req.RegisterReq
import com.hlc.mywallet.data.model.resp.SliderCaptchaResp
import com.hlc.mywallet.databinding.ActivityRegisterBinding
import com.hlc.mywallet.dialog.SliderCaptchaDialog
import com.hlc.mywallet.extension.setupPasswordVisibilityToggle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseVbActivity<ActivityRegisterBinding>() {

    private val viewModel: RegisterViewModel by viewModels()
    private var sendCountDownTimer: CountDownTimer? = null
    private var isSendCoolingDown = false
    private var hasSentOtp = false
    private var isSendingOtp = false
    private var sliderCaptchaDialog: SliderCaptchaDialog? = null

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarColorInt(ColorUtils.getColor(R.color.theme))
            navigationBarDarkIcon(false)
            fitsSystemWindows(true)
        }
    }

    override fun initView() {
        binding.apply {
            btnSend.enableWhen(etPhoneNumber) {
                canSendOtp()
            }
            btnSignUp.enableWhenAllFilled(
                etPhoneNumber,
                etOtpCode,
                etPassword,
                etConfirmPassword,
                etInviteCode
            )
            btnSend.onClick {
                if (validateBeforeSendOtp()) {
                    viewModel.sliderCaptcha()
                }
            }
            btnSignUp.onClick {
                if (validateBeforeRegister()) {
                    KeyboardUtils.hideSoftInput(this@RegisterActivity)
                    viewModel.register(
                        RegisterReq(
                            phone = etPhoneNumber.text.toString().trim(),
                            password = etPassword.text.toString().trim(),
                            confirmPassword = etConfirmPassword.text.toString().trim(),
                            otp = etOtpCode.text.toString().trim(),
                            inviteCode = etInviteCode.text.toString().trim()
                        )
                    )
                }
            }
            tvSignIn.onClick {
                finish()
            }
            etPassword.setupPasswordVisibilityToggle()
            etConfirmPassword.setupPasswordVisibilityToggle()
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

        viewModel.sendOtpState.collectWithError(
            lifecycleOwner = this,
            onLoading = { showLoading() },
            onSuccess = {
                hideLoading()
                isSendingOtp = false
                hasSentOtp = true
                Toaster.show(getString(R.string.sent_successfully))
                startSendCountDown()
            },
            onError = {
                hideLoading()
                isSendingOtp = false
            }
        )

        viewModel.registerState.collectWithError(
            lifecycleOwner = this,
            onLoading = { showLoading() },
            onSuccess = {
                hideLoading()
                Toaster.show(getString(R.string.register_success))
                finish()
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
                if (isSendingOtp) {
                    return@setOnSuccessListener
                }
                isSendingOtp = true
                sliderCaptchaDialog?.dismiss()
                viewModel.sendOtp(
                    phone = binding.etPhoneNumber.text.toString().trim(),
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
        val phone = binding.etPhoneNumber.text.toString().trim()
        if (phone.length < MIN_PHONE_LENGTH) {
            Toaster.show(getString(R.string.please_enter_a_valid_phone_number))
            return false
        }
        return true
    }

    private fun validateBeforeRegister(): Boolean {
        if (!hasSentOtp) {
            Toaster.show(getString(R.string.please_enter_your_otp))
            return false
        }
        if (!validateBeforeSendOtp()) {
            return false
        }
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        when {
            password.length < MIN_PASSWORD_LENGTH -> {
                Toaster.show(getString(R.string.please_enter_your_password))
                return false
            }
            password != confirmPassword -> {
                Toaster.show(getString(R.string.passwords_do_not_match))
                return false
            }
        }
        if (binding.etOtpCode.text.toString().trim().isEmpty()) {
            Toaster.show(getString(R.string.please_enter_your_otp))
            return false
        }
        if (binding.etInviteCode.text.toString().trim().isEmpty()) {
            Toaster.show(getString(R.string.please_enter_invite_code))
            return false
        }
        return true
    }

    private fun canSendOtp(): Boolean {
        val phone = binding.etPhoneNumber.text.toString().trim()
        return !isSendCoolingDown && phone.length >= MIN_PHONE_LENGTH
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
                binding.btnSend.text = getString(R.string.get_opt)
                binding.btnSend.isEnabled = canSendOtp()
            }
        }.start()
    }

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
