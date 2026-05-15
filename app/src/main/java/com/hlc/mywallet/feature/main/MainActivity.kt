package com.hlc.mywallet.feature.main

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.setOnClickListener
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityMainBinding
import com.hlc.mywallet.feature.wallet.WalletFragment
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

        binding.ivTool.setOnClickListener {
            selectTab(2)
        }
    }

    private fun initFragments() {
        fragments.add(HomeFragment.newInstance())
        fragments.add(DepositFragment.newInstance())
        fragments.add(WalletFragment.newInstance())
        fragments.add(TeamFragment.newInstance())
        fragments.add(MineFragment.newInstance())

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

        if (position == 2) {
            binding.ivTool.setImageResource(R.drawable.ic_nav_tools_selected)
        } else {
            binding.ivTool.setImageResource(R.drawable.ic_nav_tools)
        }

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

    fun selectTab(position: Int) {
        if (position == 2) {
            binding.ivTool.setImageResource(R.drawable.ic_nav_tools_selected)
        } else {
            binding.ivTool.setImageResource(R.drawable.ic_nav_tools)
        }
        binding.bottomNavigation.selectTab(position)
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
