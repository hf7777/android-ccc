package com.hlc.mywallet.feature.mine

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ClipboardUtils
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.loadCircle
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.setDrawablePadding
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showConfirmDialog
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.MineFunctionAdapter
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import com.hlc.mywallet.databinding.FragmentMineBinding
import com.hlc.mywallet.extension.startBreathingScaleAnimation
import com.hlc.mywallet.extension.stopBreathingScaleAnimation
import com.hlc.mywallet.feature.main.MainActivity
import com.hlc.mywallet.feature.mine.bean.MineFunction
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MineFragment : BaseVbFragment<FragmentMineBinding>() {

    private val viewModel: MineViewModel by viewModels()
    private val commonAdapter by lazy { MineFunctionAdapter() }
    private val otherAdapter by lazy { MineFunctionAdapter() }

    private var userStatistics: UserStatisticsResp? = null

    override fun initView() {
        binding.tvBalance.setDrawablePadding(leftResId = R.drawable.ic_coin, leftPadding = 4.dp, drawableWidth = 18.dp, drawableHeight = 18.dp)
        binding.tvBalanceNewbie.setDrawablePadding(leftResId = R.drawable.ic_coin, leftPadding = 4.dp, drawableWidth = 18.dp, drawableHeight = 18.dp)
        // 配置下拉刷新
        binding.apply {
            refreshLayout.apply {
                setColorSchemeResources(R.color.theme)
                setOnRefreshListener {
                    refreshData()
                }
            }
            ivCopy.onClick {
                userStatistics?.let {
                    if (!it.userCode.isNullOrEmpty()) {
                        ClipboardUtils.copyText(it.userCode)
                        Toaster.show(getString(R.string.copy_success))
                    }
                }
            }

            btnDetail.onClick {
                navigation(Routes.BILLS)
            }

            btnManage.onClick {
                (activity as? MainActivity)?.selectTab(2)
            }

            clUser.onClick {
                userStatistics?.let { statistics ->
                    Router.navigation(Routes.PERSONAL)
                        .withBundle(Bundle().apply {
                            putParcelable(Constants.RouterKeys.USER_STATISTICS, statistics)
                        })
                        .navigation(this@MineFragment)
                }
            }

            commonAdapter.setOnDebouncedItemClick { adapter, view, i ->
                when (i) {
                    0 -> {
                        // Bills
                        navigation(Routes.BILLS)
                    }
                    1 -> {
                        // Deposit
                        navigation(Routes.DEPOSIT_ORDER_LIST)
                    }
                    2 -> {
                        // Withdrawal
                        Router.navigation(Routes.BILLS)
                            .with(Constants.RouterKeys.DEFAULT_TO_WITHDRAWAL, true)
                            .navigation(this@MineFragment)
                    }
                    3 -> {
                        // Service
                        navigation(Routes.CUSTOMER_SERVICE)
                    }
                }
            }

            otherAdapter.setOnDebouncedItemClick { adapter, view, i ->
                when (i) {
                    0 -> {
                        // Bonus
                        navigation(Routes.BONUS_CENTER)
                    }
                    1 -> {
                        // Lucky Draw
                    }
                    2 -> {
                        // 绑定 tg
                        navigation(Routes.BIND_TG)
                    }
                    3 -> {
                        // Password
                        navigation(Routes.SET_PASSWORD)
                    }
                    4 -> {
                        // Pin
                        navigation(Routes.PIN)
                    }
                    5 -> {
                        // Logout
                        showConfirmDialog(
                            content = getString(R.string.are_you_sure_you_want_to_log_out)
                        ) {
                            navigation(Routes.LOGIN)
                        }
                    }
                }
            }
        }
        setupRecyclerView()
        viewModel.getUserStatistics()
    }

    private fun refreshData() {
        viewModel.getUserStatistics()
    }

    override fun observeData() {
        viewModel.userStatisticsState.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                if (!binding.refreshLayout.isRefreshing) {
                    showLoading()
                }
            },
            onSuccess = { data ->
                hideLoading()
                binding.refreshLayout.isRefreshing = false
                updateUserStatistics(data)
            },
            onError = {
                hideLoading()
                binding.refreshLayout.isRefreshing = false
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppEventBus.flow<AppEvent.NewbieSummaryUpdated>().collect(::renderNewbieBalanceUi)
            }
        }
    }

    private fun renderNewbieBalanceUi(event: AppEvent.NewbieSummaryUpdated) {
        if (event.isCompleted) {
            binding.tvBalance.visible()
            binding.llBalanceNewbie.gone()
            binding.tvCompleteTip.stopBreathingScaleAnimation()
        } else {
            binding.tvBalance.gone()
            binding.llBalanceNewbie.visible()
            binding.tvBalanceNewbie.text = event.totalReward.toString().formatNumber()
            binding.tvCompleteTip.startBreathingScaleAnimation()
        }
    }

    override fun onDestroyView() {
        binding.tvCompleteTip.stopBreathingScaleAnimation()
        super.onDestroyView()
    }

    private fun updateUserStatistics(data: UserStatisticsResp) {
        userStatistics = data
        binding.apply {
            ivAvatar.loadCircle(data.avatar ?: "")
            tvNickname.text = data.username ?: ""
            tvId.text = data.userCode ?: ""
            tvBalance.text = data.balance.formatNumber()
            tvEarnings.text = getString(R.string.today_s_earnings, data.todayEarnings ?: "0")
            tvInTransaction.text = data.inTransaction?.toString() ?: "0"
            tvTodayWithdraw.text = data.todayWithdraw ?: "0"
            tvWithdrawUpiTool.text = getString(R.string.withdraw_upi_tool, data.inWithdrawUpiTool ?:"0")
            tvTodayOrder.text = data.todayOrders?.toString() ?: "0"
            tvTodayTotal.text = data.todayTotal ?: "0"
        }
    }

    private fun setupRecyclerView() {
        val commonFunction = arrayListOf(
            MineFunction(R.drawable.ic_mine_bills, getString(R.string.bills)),
            MineFunction(R.drawable.ic_mine_deposit, getString(R.string.deposit)),
            MineFunction(R.drawable.ic_mine_withdraw, getString(R.string.withdrawal)),
            MineFunction(R.drawable.ic_mine_service, getString(R.string.service))
        )

        val otherFunction = arrayListOf(
            MineFunction(R.drawable.ic_mine_bonus, getString(R.string.bonus)),
            MineFunction(R.drawable.ic_mine_lucky, getString(R.string.lucky_draw)),
            MineFunction(R.drawable.ic_mine_box, getString(R.string.bind_telegram)),
            MineFunction(R.drawable.ic_mine_password, getString(R.string.password)),
            MineFunction(R.drawable.ic_mine_pin, getString(R.string.pin)),
            MineFunction(R.drawable.ic_mine_logout, getString(R.string.logout)),
        )

        binding.rvCommon.apply {
            layoutManager = object : GridLayoutManager(requireContext(), 4) {
                override fun canScrollVertically(): Boolean = false
            }
            adapter = commonAdapter
            addItemDecoration(
                SpaceItemDecoration.Builder()
                    .spacing(10.dp)
                    .build()
            )
        }
        commonAdapter.submitList(commonFunction)

        binding.rvOther.apply {
            layoutManager = object : GridLayoutManager(requireContext(), 4) {
                override fun canScrollVertically(): Boolean = false
            }
            adapter = otherAdapter
            addItemDecoration(
                SpaceItemDecoration.Builder()
                    .spacing(10.dp)
                    .build()
            )
        }
        otherAdapter.submitList(otherFunction)
    }

    companion object {
        fun newInstance() = MineFragment()
    }
}
