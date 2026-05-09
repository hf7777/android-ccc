package com.hlc.mywallet.feature.main

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityMainBinding
import com.hlc.mywallet.feature.add.AddFragment
import com.hlc.mywallet.feature.deposit.DepositFragment
import com.hlc.mywallet.feature.home.HomeFragment
import com.hlc.mywallet.feature.mine.MineFragment
import com.hlc.mywallet.feature.team.TeamFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseVbActivity<ActivityMainBinding>() {

    private val viewModel: MainViewModel by viewModels()
    private val fragments = mutableListOf<Fragment>()
    private var currentPosition = 0

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarDarkIcon(true)
            navigationBarColorInt(ColorUtils.getColor(R.color.white))
            fitsSystemWindows(true)
        }
    }

    override fun initView() {
        initFragments()
        setupBottomNavigation()
        loadMyWallet()
    }

    private fun initFragments() {
        fragments.add(HomeFragment.Companion.newInstance())
        fragments.add(DepositFragment.Companion.newInstance())
        fragments.add(AddFragment.Companion.newInstance())
        fragments.add(TeamFragment.Companion.newInstance())
        fragments.add(MineFragment.Companion.newInstance())

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragments[0])
            .commit()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnTabSelectedListener { position ->
            switchFragment(position)
        }
    }

    private fun switchFragment(position: Int) {
        if (position == currentPosition) return

        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(fragments[currentPosition])

        if (fragments[position].isAdded) {
            transaction.show(fragments[position])
        } else {
            transaction.add(R.id.fragment_container, fragments[position])
        }

        transaction.commit()
        currentPosition = position
    }

    /**
     * 启动获取钱包数据缓存到本地，方便后续使用
     */
    private fun loadMyWallet() {
        viewModel.myWalletResultFlow.collectWithError(
            lifecycleOwner = this,
            onSuccess = { wallets ->
                // 钱包数据已缓存，可在其他地方使用
            }
        )
        viewModel.getMyWallet()
    }
}
