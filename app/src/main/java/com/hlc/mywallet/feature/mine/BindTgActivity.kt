package com.hlc.mywallet.feature.mine

import androidx.activity.viewModels
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
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
import com.hlc.mywallet.feature.main.MainViewModel
import com.hlc.mywallet.feature.wallet.WalletViewModel
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BindTgActivity : BaseVbActivity<ActivityBindTgBinding>() {

    private val mainViewModel: MainViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    private var bindCodeResp: BindCodeResp? = null

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
            ivCopyName.onClick {
                ClipboardUtils.copyText(tvBotName.text.toString())
                Toaster.show(R.string.copy_success)
            }
            ivCopyMsg.onClick {
                ClipboardUtils.copyText(tvBindMsg.text.toString())
                Toaster.show(R.string.copy_success)
            }
            btnRefresh.onClick {
                checkBind()
            }
            btnOpen.onClick {
                bindCodeResp?.let {
                    Router.navigation(it.redirectUrl ?: "").navigation(this@BindTgActivity)
                }
            }
        }
    }

    private fun checkBind() {
        walletViewModel.checkBinding()
    }

    override fun initData() {
        mainViewModel.getBindCode()
    }

    override fun observeData() {
        mainViewModel.bindCodeResultFlow.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                showPageLoading()
            },
            onSuccess = { data ->
                showPageContent()
                updateUi(data)
            },
            onError = {
                showPageError {
                    mainViewModel.getBindCode()
                }
            }
        )

        walletViewModel.checkBindingFlow.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                showLoading()
            },
            onSuccess = { data ->
                checkBinding(data)
                hideLoading()
            },
            onError = { errorMsg ->
                hideLoading()
            }
        )
    }

    private fun checkBinding(data: CheckBindingResp) {
        if (!data.tgBound) {
            // 去绑定Tg
            showConfirmDialog(content = StringUtils.getString(R.string.please_bind_telegram), showCancelButton = false) {

            }
        } else {
            Toaster.show(getString(R.string.binding_successful))
            finish()
        }
    }

    private fun updateUi(bindCodeResp: BindCodeResp) {
        this.bindCodeResp = bindCodeResp
        binding.apply {
            tvBotName.text = bindCodeResp.botName
            tvBindMsg.text = bindCodeResp.bindMessage
        }
    }

    override fun useBaseTitleBar(): Boolean = true

    override fun getBaseTitleBarTitle(): String = StringUtils.getString(R.string.bind_telegram)
}