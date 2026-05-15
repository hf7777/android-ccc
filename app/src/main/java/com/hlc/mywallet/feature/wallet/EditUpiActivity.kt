package com.hlc.mywallet.feature.wallet

import android.os.Bundle
import androidx.activity.viewModels
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hjq.toast.Toaster
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.Wallet
import com.hlc.mywallet.databinding.ActivityEditUpiBinding
import com.hlc.mywallet.dialog.StringSelectDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditUpiActivity : BaseVbActivity<ActivityEditUpiBinding>() {

    private var wallet: Wallet? = null
    private var upiList: List<String> = emptyList()
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        wallet = intent.getParcelableExtra(KEY_WALLET)
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        binding.apply {
            tvPartner.text = wallet?.channelName.orEmpty()
            tvAccount.text = wallet?.phone.orEmpty()

            tvUpi.onClick {
                if (upiList.isNotEmpty()) {
                    StringSelectDialog.newInstance(upiList, binding.tvUpi.text.toString().trim())
                        .setOnItemSelectedListener { selectedUpi ->
                            binding.tvUpi.text = selectedUpi
                        }
                        .show(supportFragmentManager, StringSelectDialog::class.java.simpleName)
                }
            }

            btnConfirm.onClick {
                submitEditUpi()
            }
        }

        wallet?.let {
            val phone = it.phone.orEmpty()
            val channelCode = it.channelCode.orEmpty()
            if (phone.isNotEmpty() && channelCode.isNotEmpty()) {
                walletViewModel.getUpiList(phone, channelCode)
            }
        }
    }

    override fun observeData() {
        walletViewModel.upiListFlow.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                showLoading()
            },
            onSuccess = { data ->
                hideLoading()
                upiList = data
                val currentUpi = wallet?.upi.orEmpty()
                binding.tvUpi.text = data.find { it == currentUpi } ?: data.firstOrNull().orEmpty()
                binding.btnConfirm.isEnabled = data.isNotEmpty()
            },
            onError = {
                hideLoading()
            }
        )
        walletViewModel.editUpiFlow.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                showLoading()
            },
            onSuccess = {
                hideLoading()
                AppEventBus.post(AppEvent.WalletRefreshRequested)
                finish()
            },
            onError = {
                hideLoading()
            }
        )
    }

    private fun submitEditUpi() {
        val phone = wallet?.phone.orEmpty()
        val channelCode = wallet?.channelCode.orEmpty()
        val newUpi = binding.tvUpi.text.toString().trim()
        if (phone.isEmpty() || channelCode.isEmpty() || newUpi.isEmpty()) {
            Toaster.show(getString(R.string.request_failed))
            return
        }
        walletViewModel.editUpi(phone, channelCode, newUpi)
    }

    override fun useBaseTitleBar(): Boolean = true

    override fun getBaseTitleBarTitle(): String? = StringUtils.getString(R.string.edit_upi)

    companion object {
        const val KEY_WALLET = "wallet"
    }

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarColorInt(ColorUtils.getColor(R.color.white))
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }
}
