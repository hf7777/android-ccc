package com.hlc.mywallet.feature.mine.bills

import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityBillsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillsActivity : BaseVbActivity<ActivityBillsBinding>() {

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

    override fun getBaseTitleBarTitle(): String = StringUtils.getString(R.string.bills)

    override fun initView() {
        val defaultToWithdrawal = intent.getBooleanExtra(KEY_DEFAULT_TO_WITHDRAWAL, false)
        if (supportFragmentManager.findFragmentById(R.id.fl_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, BillsFragment.newInstance(defaultToWithdrawal))
                .commit()
        }
    }

    companion object {
        const val KEY_DEFAULT_TO_WITHDRAWAL = "default_to_withdrawal"
    }
}
