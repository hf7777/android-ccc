package com.hlc.mywallet.feature.main

import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import android.os.Bundle
import com.hjq.toast.Toaster
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.LogUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.mywallet.extension.playQuickScaleAnim
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.extension.setOnClickListener
import com.hlc.mywallet.R
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.common.AppUpdateCheckEvent
import com.hlc.mywallet.common.PermissionRequester
import com.hlc.mywallet.common.showUpdateDialog
import com.hlc.mywallet.data.model.resp.BulletinResp
import com.hlc.mywallet.databinding.ActivityMainBinding
import com.hlc.mywallet.dialog.BulletinDialog
import com.hlc.mywallet.dialog.ChoosePlanDialog
import com.hlc.mywallet.dialog.StatusDescDialog
import com.hlc.mywallet.dialog.UpdateDialog
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

    private val pendingBulletins = ArrayDeque<BulletinResp>()
    private var currentPosition = 0
    private var skipNextBulletinChain = false
    private var lastBackPressTime = 0L
    private var restoredInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        restoredInstanceState = savedInstanceState
        super.onCreate(savedInstanceState)
    }

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
        initFragments(restoredInstanceState)
        setupBottomNavigation()
        setupDoubleBackToExit()
        requestNotificationPermissionIfNeeded()
        loadMyWallet()
        loadBulletins()

        binding.ivTool.setOnClickListener {
            binding.ivTool.playQuickScaleAnim()
            selectTab(2)
        }

    }

    override fun observeData() {
        observeBulletins()
        observeConfirmBulletin()
        observeNewbieSummary()
        observeAppUpdate()
    }

    override fun onResume() {
        super.onResume()
        showNextBulletinIfNeeded()
        bonusViewModel.getNewbieSummary()
        if (supportFragmentManager.findFragmentByTag(UpdateDialog.TAG) == null) {
            viewModel.checkUpdate(manual = false)
        }
    }

    private fun initFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0)
        }

        val transaction = supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)

        for (i in 0 until TAB_COUNT) {
            val fragment = getOrCreateTabFragment(i)
            when {
                fragment.isAdded && i == currentPosition -> transaction.show(fragment)
                fragment.isAdded -> transaction.hide(fragment)
                i == currentPosition -> transaction.add(
                    R.id.fragment_container,
                    fragment,
                    makeTabTag(i)
                )
            }
        }

        if (!supportFragmentManager.isStateSaved) {
            transaction.commitNow()
        }
    }

    private fun getOrCreateTabFragment(position: Int): Fragment {
        return supportFragmentManager.findFragmentByTag(makeTabTag(position))
            ?: createTabFragment(position)
    }

    private fun createTabFragment(position: Int): Fragment = when (position) {
        0 -> HomeFragment.newInstance()
        1 -> DepositFragment.newInstance()
        2 -> WalletFragment.newInstance()
        3 -> TeamFragment.newInstance()
        4 -> MineFragment.newInstance()
        else -> throw IllegalArgumentException("Invalid tab position: $position")
    }

    private fun makeTabTag(position: Int) = "$TAB_TAG_PREFIX$position"

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnTabSelectedListener { position ->
            switchFragment(position)
        }
    }

    /** 首页连续按两次返回键才退出应用 */
    private fun setupDoubleBackToExit() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val now = System.currentTimeMillis()
                    if (now - lastBackPressTime <= BACK_PRESS_INTERVAL_MS) {
                        finishAffinity()
                        return
                    }
                    lastBackPressTime = now
                    Toaster.show(getString(R.string.press_back_again_to_exit))
                }
            }
        )
    }

    private fun switchFragment(position: Int) {
        if (position == currentPosition) return
        if (supportFragmentManager.isStateSaved) return

        updateToolIcon(position)

        val transaction = supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .hide(getOrCreateTabFragment(currentPosition))

        val target = getOrCreateTabFragment(position)
        if (target.isAdded) {
            transaction.show(target)
        } else {
            transaction.add(R.id.fragment_container, target, makeTabTag(position))
        }

        transaction.commitNow()
        currentPosition = position
    }

    private fun updateToolIcon(position: Int) {
        if (position == 2) {
            binding.ivTool.setImageResource(R.drawable.ic_nav_tools_selected)
        } else {
            binding.ivTool.setImageResource(R.drawable.ic_nav_tools)
        }
    }

    fun selectTab(position: Int) {
        if (isFinishing || isDestroyed || supportFragmentManager.isStateSaved) return
        updateToolIcon(position)
        binding.bottomNavigation.selectTab(position)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (!PermissionRequester.hasNotificationPermission(this)) {
            PermissionRequester.requestNotificationPermission(this) { granted ->
                LogUtils.d("Notification permission granted: $granted")
            }
        }
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

    private fun observeAppUpdate() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateCheckEvent.collect { event ->
                    when (event) {
                        is AppUpdateCheckEvent.HasUpdate -> showUpdateDialog(event.version)
                        else -> Unit
                    }
                }
            }
        }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_POSITION, currentPosition)
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
        private const val BACK_PRESS_INTERVAL_MS = 2000L
        private const val KEY_CURRENT_POSITION = "current_position"
        private const val TAB_TAG_PREFIX = "main_tab_"
        private const val TAB_COUNT = 5
    }
}
