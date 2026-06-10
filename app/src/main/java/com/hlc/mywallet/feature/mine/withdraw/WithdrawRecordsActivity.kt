package com.hlc.mywallet.feature.mine.withdraw

import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityWithdrawRecordsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawRecordsActivity : BaseVbActivity<ActivityWithdrawRecordsBinding>() {

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarColorInt(ColorUtils.getColor(R.color.white))
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }

    override fun useBaseTitleBar(): Boolean = true

    override fun getBaseTitleBarTitle(): String = getString(R.string.withdrawal_records)

    override fun initView() {
        if (supportFragmentManager.findFragmentById(R.id.fl_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, WithdrawRecordsFragment.newInstance())
                .commit()
        }
    }
}
