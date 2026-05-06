package com.hlc.mywallet.feature.tutorial

import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityTutorialBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialActivity : BaseVbActivity<ActivityTutorialBinding>() {

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
        binding.titleBar.setOnBackClickListener {
            finish()
        }

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TutorialFragment.newInstance())
                .commit()
        }
    }
}
