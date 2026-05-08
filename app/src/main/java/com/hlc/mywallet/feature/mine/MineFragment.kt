package com.hlc.mywallet.feature.mine

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ClipboardUtils
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.loadCircle
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showConfirmDialog
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.MineFunctionAdapter
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import com.hlc.mywallet.databinding.FragmentMineBinding
import com.hlc.mywallet.feature.mine.bean.MineFunction
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineFragment : BaseVbFragment<FragmentMineBinding>() {

    private val viewModel: MineViewModel by viewModels()
    private val commonAdapter by lazy { MineFunctionAdapter() }
    private val otherAdapter by lazy { MineFunctionAdapter() }

    private var userStatistics: UserStatisticsResp? = null

    override fun initView() {
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

            commonAdapter.setOnDebouncedItemClick { adapter, view, i ->
                when (i) {
                    0 -> {
                        // Bills
                    }
                    1 -> {
                        // Deposit
                    }
                    2 -> {
                        // Withdrawal
                    }
                    3 -> {
                        // Service
                    }
                }
            }

            otherAdapter.setOnDebouncedItemClick { adapter, view, i ->
                when (i) {
                    0 -> {
                        // Bonus
                    }
                    1 -> {
                        // Lucky Draw
                    }
                    2 -> {
                        // Inbox
                    }
                    3 -> {
                        // Password
                    }
                    4 -> {
                        // Pin
                    }
                    5 -> {
                        // Language
                    }
                    6 -> {
                        // Settings
                    }
                    7 -> {
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
            MineFunction(R.drawable.ic_mine_box, getString(R.string.inbox)),
            MineFunction(R.drawable.ic_mine_password, getString(R.string.password)),
            MineFunction(R.drawable.ic_mine_pin, getString(R.string.pin)),
            MineFunction(R.drawable.ic_mine_language, getString(R.string.language)),
            MineFunction(R.drawable.ic_mine_settings, getString(R.string.settings)),
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
