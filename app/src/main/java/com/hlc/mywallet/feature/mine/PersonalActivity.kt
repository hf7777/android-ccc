package com.hlc.mywallet.feature.mine

import android.os.Bundle
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.mywallet.R
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import com.hlc.mywallet.databinding.ActivityPersonalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalActivity : BaseVbActivity<ActivityPersonalBinding>() {

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
        }
    }

    override fun useBaseTitleBar(): Boolean = true

    override fun getBaseTitleBarTitle(): String {
        return getString(R.string.personal_info)
    }

}
