package com.hlc.mywallet.feature.wallet

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.common.ActivityStackManager
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.dialog.StringSelectDialog
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.FragmentPayChannelSetup3Binding
import com.hlc.mywallet.feature.wallet.bean.PayChannelSetupArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * @author Wade
 * @since 2026/5/13
 */
@AndroidEntryPoint
class PayChannelSetup3Fragment: BaseVbFragment<FragmentPayChannelSetup3Binding>() {

    private var setupArgs: PayChannelSetupArgs? = null
    private var upiList: List<String> = emptyList()

    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArgs = arguments?.getParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS)
    }

    override fun initView() {
        binding.apply {
            setupArgs?.let { args ->
                if (args.phone.isNotEmpty()) {
                    walletViewModel.getUpiList(args.phone, args.channelCode)
                }
            }
            btnFinish.isEnabled = false
            tvUpi.onClick {
                if (upiList.isNotEmpty()) {
                    StringSelectDialog.newInstance(upiList, binding.tvUpi.text.toString().trim())
                        .setOnItemSelectedListener { selectedUpi ->
                            binding.tvUpi.text = selectedUpi
                        }
                        .show(parentFragmentManager, StringSelectDialog::class.java.simpleName)
                }
            }
            btnFinish.onClick {
                addWallet()
            }
        }
    }

    override fun observeData() {
        walletViewModel.upiListFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = { data ->
                hideLoading()
                upiList = data
                binding.tvUpi.text = data.firstOrNull().orEmpty()
                binding.btnFinish.isEnabled = data.isNotEmpty()
            },
            onError = {
                hideLoading()
            }
        )
        walletViewModel.addWalletFlow.collectWithError(
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
            onError = {
                hideLoading()
            }
        )
    }

    private fun addWallet() {
        val channelCode = setupArgs?.channelCode.orEmpty()
        val phone = setupArgs?.phone.orEmpty()
        val pin = setupArgs?.pin.orEmpty()
        val otp = setupArgs?.otp.orEmpty()
        val upiId = binding.tvUpi.text.toString().trim()
        if (phone.isEmpty() || pin.isEmpty() || otp.isEmpty() || channelCode.isEmpty() || upiId.isEmpty()) {
            Toaster.show(getString(R.string.request_failed))
            return
        }
        walletViewModel.addWallet(
            phone = phone,
            channelCode = channelCode,
            otp = otp,
            pin = pin,
            upiId = upiId
        )
    }

    companion object {
        fun newInstance(setupArgs: PayChannelSetupArgs?) = PayChannelSetup3Fragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS, setupArgs)
            }
        }
    }
}
