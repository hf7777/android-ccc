package com.hlc.mywallet.feature.mine

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.common.AppUpdateCheckEvent
import com.hlc.mywallet.common.showUpdateDialog
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import com.hlc.mywallet.databinding.ActivityPersonalBinding
import com.hlc.mywallet.feature.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalActivity : BaseVbActivity<ActivityPersonalBinding>() {

    private val viewModel: MainViewModel by viewModels()
    private var userStatistics: UserStatisticsResp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        userStatistics = intent.getParcelableExtra(Constants.RouterKeys.USER_STATISTICS)
        super.onCreate(savedInstanceState)
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

    override fun initView() {
        binding.apply {
            tvUsername.text = userStatistics?.username.orEmpty()
            tvId.text = userStatistics?.userCode.orEmpty()
            tvPhone.text = userStatistics?.phone.orEmpty()
            tvVersion.text = "v${AppUtils.getAppVersionName()}"
            btnCheck.onClick {
                showLoading()
                viewModel.checkUpdate(manual = true)
            }
        }
    }

    override fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateCheckEvent.collect { event ->
                    when (event) {
                        is AppUpdateCheckEvent.HasUpdate -> showUpdateDialog(event.version)
                        AppUpdateCheckEvent.UpToDate -> {
                            hideLoading()
                            Toaster.show(getString(R.string.app_update_already_latest))
                        }
                        is AppUpdateCheckEvent.Failed -> {
                            hideLoading()
                            if (event.message.isNotBlank()) {
                                Toaster.show(event.message)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun useBaseTitleBar(): Boolean = true

    override fun getBaseTitleBarTitle(): String {
        return getString(R.string.personal_info)
    }
}
