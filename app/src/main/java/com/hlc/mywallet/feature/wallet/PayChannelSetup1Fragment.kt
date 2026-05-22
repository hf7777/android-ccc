package com.hlc.mywallet.feature.wallet

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
import com.hlc.mywallet.databinding.FragmentPayChannelSetup1Binding
import com.hlc.mywallet.extension.setupPasswordVisibilityToggle
import com.hlc.mywallet.feature.main.MainViewModel
import com.hlc.mywallet.feature.wallet.bean.PayChannelSetupArgs
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Wade
 * @since 2026/5/13
 */
@AndroidEntryPoint
class PayChannelSetup1Fragment: BaseVbFragment<FragmentPayChannelSetup1Binding>() {

    private var setupArgs: PayChannelSetupArgs? = null

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArgs = arguments?.getParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS)
    }

    override fun initView() {
        binding.apply {
            setupArgs?.let { args ->
                tvAuthorize.text = StringUtils.format(StringUtils.getString(R.string.authorize_x), args.channelName)
                tvTips.text = StringUtils.format(StringUtils.getString(R.string.let_up_enjoy_the_work), args.channelName)
            }

            val phone = setupArgs?.phone.orEmpty()
            if (phone.isNotEmpty()) {
                etPhone.setText(phone)
                etPhone.isEnabled = false
                etPhone.isFocusable = false
                etPhone.isFocusableInTouchMode = false
                etPhone.isClickable = false
                etPhone.isLongClickable = false
                etPhone.isCursorVisible = false
            }

            btnNext.enableWhen(etPhone, etPin) {
                val inputPhone = etPhone.text.toString().trim()
                val pin = etPin.text.toString().trim()
                inputPhone.isNotEmpty() && pin.isNotEmpty() && pin.length == 6
            }

            btnNext.onClick {
                checkPin()
            }
            etPin.setupPasswordVisibilityToggle()
        }
    }

    override fun observeData() {
        viewModel.checkPinState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = { data ->
                hideLoading()
                if (data) {
                    val stepArgs = setupArgs?.copy(
                        phone = binding.etPhone.text.toString().trim(),
                        pin = binding.etPin.text.toString().trim()
                    ) ?: return@collectWithError
                    (activity as? PayChannelSetupActivity)?.navigateToStep2(stepArgs)
                } else {
                    Toaster.show(getString(R.string.incorrect_pin))
                }
            },
            onError = {
                Toaster.show(getString(R.string.request_failed))
                hideLoading()
            }
        )
    }

    private fun checkPin() {
        val pin = binding.etPin.text.toString().trim()
        viewModel.checkPin(pin)
    }

    companion object {

        fun newInstance(setupArgs: PayChannelSetupArgs?) = PayChannelSetup1Fragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS, setupArgs)
            }
        }
    }
}
