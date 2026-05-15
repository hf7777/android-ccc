package com.hlc.mywallet.feature.wallet

import android.os.Bundle
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityPayChannelSetupBinding
import com.hlc.mywallet.feature.wallet.bean.PayChannelSetupArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayChannelSetupActivity : BaseVbActivity<ActivityPayChannelSetupBinding>() {

    private var setupArgs: PayChannelSetupArgs? = null
    private val backStackChangedListener = {
        updateStepProgress()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setupArgs = intent.getParcelableExtra(KEY_SETUP_ARGS)
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        supportFragmentManager.addOnBackStackChangedListener(backStackChangedListener)
        if (supportFragmentManager.findFragmentById(R.id.fl_container) == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fl_container,
                    PayChannelSetup1Fragment.newInstance(setupArgs)
                )
                .commit()
        }
        binding.vStep.setCurrentStep(1)
    }

    override fun getBaseTitleBarTitle(): String {
        return setupArgs?.channelName.orEmpty()
    }

    override fun useBaseTitleBar(): Boolean = true

    override fun onDestroy() {
        supportFragmentManager.removeOnBackStackChangedListener(backStackChangedListener)
        super.onDestroy()
    }

    fun navigateToStep2(stepArgs: PayChannelSetupArgs) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fl_container,
                PayChannelSetup2Fragment.newInstance(stepArgs)
            )
            .addToBackStack(PayChannelSetup2Fragment::class.java.simpleName)
            .commit()
        binding.vStep.setCurrentStep(2)
    }

    fun navigateToStep3(stepArgs: PayChannelSetupArgs) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fl_container,
                PayChannelSetup3Fragment.newInstance(stepArgs)
            )
            .addToBackStack(PayChannelSetup3Fragment::class.java.simpleName)
            .commit()
        binding.vStep.setCurrentStep(3)
    }

    private fun updateStepProgress() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fl_container)
        binding.vStep.setCurrentStep(
            when (currentFragment) {
                is PayChannelSetup3Fragment -> 3
                is PayChannelSetup2Fragment -> 2
                else -> 1
            }
        )
    }

    companion object {
        const val KEY_SETUP_ARGS = "setup_args"
    }
}
