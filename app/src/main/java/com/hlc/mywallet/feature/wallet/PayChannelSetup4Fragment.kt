package com.hlc.mywallet.feature.wallet

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.enableWhenAllFilled
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.common.ActivityStackManager
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.req.WalletBankInfoReq
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.FragmentPayChannelSetup4Binding
import com.hlc.mywallet.extension.setupPasswordVisibilityToggle
import com.hlc.mywallet.feature.wallet.bean.PayChannelSetupArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PayChannelSetup4Fragment : BaseVbFragment<FragmentPayChannelSetup4Binding>() {

    private var setupArgs: PayChannelSetupArgs? = null

    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArgs = arguments?.getParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS)
    }

    override fun initView() {
        binding.apply {
            val upiId = setupArgs?.upiId.orEmpty()
            tvUpiDisplay.text = if (upiId.isNotEmpty()) {
                getString(R.string.upi_format, upiId)
            } else {
                ""
            }

            etUpiPin.setupPasswordVisibilityToggle()

            btnFinish.enableWhenAllFilled(
                etAccountNo,
                etBankName,
                etUpiPin
            )

            btnFinish.onClick {
                submitBankCard()
            }
        }
    }

    override fun observeData() {
        walletViewModel.walletBankCardFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = {
                hideLoading()
                AppEventBus.post(AppEvent.WalletRefreshRequested)
                ActivityStackManager.finishActivities(PayChannelActivity::class.java)
                activity?.finish()
            },
            onError = { message ->
                hideLoading()
                Toaster.show(message)
            }
        )
    }

    private fun submitBankCard() {
        val walletId = setupArgs?.walletId.orEmpty()
        if (walletId.isEmpty()) {
            Toaster.show(getString(R.string.request_failed))
            return
        }
        val req = WalletBankInfoReq(
            walletId = walletId,
            accountNo = binding.etAccountNo.text.toString().trim(),
            bankName = binding.etBankName.text.toString().trim(),
            upiPin = binding.etUpiPin.text.toString().trim()
        )
        walletViewModel.checkUpiPinAndAddWalletBankCard(req, isEditMode = false)
    }

    companion object {
        fun newInstance(setupArgs: PayChannelSetupArgs?) = PayChannelSetup4Fragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS, setupArgs)
            }
        }
    }
}