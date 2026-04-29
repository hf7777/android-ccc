package com.hlc.mywallet.feature.login

import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.R
import com.hlc.mywallet.databinding.ActivityLoginBinding

class LoginActivity : BaseVbActivity<ActivityLoginBinding>() {

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

    }
}