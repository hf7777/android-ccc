package com.hlc.mywallet.feature.register

import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityRegisterBinding

class RegisterActivity : BaseVbActivity<ActivityRegisterBinding>() {


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
        binding.tvSignIn.onClick {
            finish()
        }
    }
}