package com.hlc.mywallet.feature.main

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.LogUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.extension.setOnClickListener
import com.hlc.mywallet.R
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.data.model.resp.BulletinResp
import com.hlc.mywallet.databinding.ActivityMainBinding
import com.hlc.mywallet.dialog.BulletinDialog
import com.hlc.mywallet.feature.bonus.BonusViewModel
import com.hlc.mywallet.feature.wallet.WalletFragment
import com.hlc.mywallet.feature.deposit.DepositFragment
import com.hlc.mywallet.feature.home.HomeFragment
import com.hlc.mywallet.feature.mine.MineFragment
import com.hlc.mywallet.feature.team.TeamFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseVbActivity<ActivityMainBinding>() {

    private val viewModel: MainViewModel by viewModels()
    private val bonusViewModel: BonusViewModel by viewModels()

    private val fragments = mutableListOf<Fragment>()
    private val pendingBulletins = ArrayDeque<BulletinResp>()
    private var currentPosition = 0
    private var skipNextBulletinChain = false

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
        loadBulletins()

        binding.ivTool.setOnClickListener {
            selectTab(2)
        }
    }

    override fun observeData() {
        observeBulletins()
        observeConfirmBulletin()
        observeNewbieSummary()
    }

    override fun onResume() {
        super.onResume()
        showNextBulletinIfNeeded()
        // 查询完成新手任务没有，没有则需要显示引导UI
        bonusViewModel.getNewbieSummary()
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

    private fun loadBulletins() {
        viewModel.getBulletinList()
    }

    /**
     * 公告是启动后的一次性串行弹窗链路。
     * 数据到达后先缓存到队列里，Dialog 关闭时再继续取下一条，避免多个弹窗同时叠在一起。
     */
    private fun observeBulletins() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bulletinListState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            pendingBulletins.clear()
                            pendingBulletins.addAll(
                                result.data
                                    .filter { it.status.equals(BULLETIN_STATUS_ENABLE, ignoreCase = true) }
                                    .sortedBy { it.sortOrder ?: Int.MAX_VALUE }
                            )
                            viewModel.consumeBulletinListState()
                            showNextBulletinIfNeeded()
                        }
                        is ApiResult.Error -> {
                            LogUtils.w("bulletinList failed: ${result.exception.message}")
                            viewModel.consumeBulletinListState()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeConfirmBulletin() {
        viewModel.confirmBulletinState.collectWithError(
            lifecycleOwner = this,
            onSuccess = {
                LogUtils.d("confirmBulletin success")
            }
        )
    }

    private fun observeNewbieSummary() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bonusViewModel.newbieSummaryState.collect { result ->
                    if (result is ApiResult.Success) {
                        val summary = result.data
                        AppEventBus.post(
                            AppEvent.NewbieSummaryUpdated(
                                isCompleted = summary.isCompleted == 1,
                                totalReward = summary.totalReward
                            )
                        )
                    }
                }
            }
        }
    }

    private fun showNextBulletinIfNeeded() {
        if (isFinishing || isDestroyed || supportFragmentManager.isStateSaved) {
            return
        }
        if (supportFragmentManager.findFragmentByTag(BulletinDialog.TAG) != null) {
            return
        }
        val bulletin = pendingBulletins.removeFirstOrNull() ?: return
        skipNextBulletinChain = false
        BulletinDialog.newInstance(bulletin)
            .setOnPrimaryActionListener { currentBulletin ->
                if (currentBulletin.autoConfirm.equals(AUTO_CONFIRM_GO, ignoreCase = true)) {
                    skipNextBulletinChain = openBulletinRoute(currentBulletin)
                } else {
                    viewModel.confirmBulletin(currentBulletin.id.orEmpty())
                }
            }
            .setOnDismissListener {
                if (skipNextBulletinChain) {
                    skipNextBulletinChain = false
                    return@setOnDismissListener
                }
                if (isFinishing || isDestroyed) return@setOnDismissListener
                val decorView = window?.decorView ?: return@setOnDismissListener
                decorView.post {
                    if (isFinishing || isDestroyed) return@post
                    supportFragmentManager.executePendingTransactions()
                    showNextBulletinIfNeeded()
                }
            }
            .show(supportFragmentManager, BulletinDialog.TAG)
    }

    private fun openBulletinRoute(bulletin: BulletinResp): Boolean {
        val route = bulletin.jumpRoute?.trim().orEmpty()
        if (route.isEmpty()) {
            return false
        }
        return Router.navigation(route).navigation(this)
    }

    companion object {
        private const val BULLETIN_STATUS_ENABLE = "enable"
        private const val AUTO_CONFIRM_GO = "N"
    }
}
