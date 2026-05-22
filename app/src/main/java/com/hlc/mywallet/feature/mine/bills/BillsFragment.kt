package com.hlc.mywallet.feature.mine.bills

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.QuickAdapterHelper
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.buildAdapterHelper
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.BillsAdapter
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.common.DateMonthUtils
import com.hlc.mywallet.data.model.resp.BalanceType
import com.hlc.mywallet.data.model.resp.Bill
import com.hlc.mywallet.databinding.HeaderBillListBinding
import com.hlc.mywallet.dialog.BalanceTypeSelectDialog
import com.hlc.mywallet.dialog.StringSelectDialog
import com.hlc.mywallet.feature.main.MainViewModel
import com.hlc.mywallet.feature.mine.MineViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Wade
 * @since 2026/5/14
 */
@AndroidEntryPoint
class BillsFragment : BaseLazyListFragment<Bill, BillsAdapter>() {

    private val viewModel: MineViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val dateList by lazy { DateMonthUtils.getRecentYearMonths() }
    private var defaultToWithdrawal = false
    private var defaultToActivity = false
    private var listHelper: QuickAdapterHelper? = null
    private var selectedYearMonth = DateMonthUtils.getCurrentYearMonth()
    private var balanceTypeList: List<BalanceType> = emptyList()
    private var selectedBalanceType: BalanceType? = null
    private var headerBinding: HeaderBillListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultToWithdrawal = arguments?.getBoolean(Constants.RouterKeys.DEFAULT_TO_WITHDRAWAL, false) == true
        defaultToActivity = arguments?.getBoolean(Constants.RouterKeys.DEFAULT_TO_ACTIVITY, false) == true
    }

    override fun initView() {
        super.initView()
        refreshLayout.setBackgroundResource(R.color.bg_0f_theme)
        getBalanceType()
    }

    override fun onListViewCreated() {
        addHeaderLayout()
        listHelper = listAdapter.buildAdapterHelper(
            headerSize = 10.dp,
            footerSize = 20.dp
        )
        recyclerView.adapter = listHelper?.adapter
    }

    override fun createAdapter(): BillsAdapter {
        return BillsAdapter()
    }

    override fun requestListData(page: Int) {
        if ((defaultToWithdrawal || defaultToActivity) && selectedBalanceType == null) {
            return
        }
        requestBills()
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext())
    }

    override fun createItemDecoration() = SpaceItemDecoration.Builder()
        .verticalSpacing(10.dp)
        .build()

    override fun enableRefresh(): Boolean = true

    override fun enableLoadMore(): Boolean = false

    override fun observeData() {
        viewModel.billsFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onSuccess = { data ->
                onListLoadSuccess(data.rows, false)
            },
            onError = { errorMsg ->
                onListLoadError(errorMsg)
            }
        )

        mainViewModel.balanceTypeState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onSuccess = { balanceType ->
                balanceTypeList = buildBalanceTypeOptions(balanceType)
                if (selectedBalanceType == null) {
                    selectedBalanceType = if (defaultToWithdrawal) {
                        balanceTypeList.firstOrNull {
                            it.value == DEFAULT_TYPE_WITHDRAWAL
                        } ?: balanceTypeList.firstOrNull { it.value == null }
                    } else if (defaultToActivity) {
                        balanceTypeList.firstOrNull {
                            it.value == DEFAULT_TYPE_ACTIVITY_REWARD
                        } ?: balanceTypeList.firstOrNull { it.value == null }
                    } else {
                        balanceTypeList.firstOrNull { it.value == null }
                    }
                    headerBinding?.tvType?.text = selectedBalanceType?.label.orEmpty()
                    if (defaultToWithdrawal || defaultToActivity) {
                        requestBills()
                    }
                }
            },
            onError = { errorMsg ->

            }
        )
    }

    private fun getBalanceType() {
        mainViewModel.antBalanceType()
    }

    private fun requestBills() {
        viewModel.geBills(
            yearMonth = selectedYearMonth,
            type = selectedBalanceType?.value
        )
    }

    private fun buildBalanceTypeOptions(balanceType: List<BalanceType>): List<BalanceType> {
        return listOf(
            BalanceType(
                label = getString(R.string.all),
                value = null
            )
        ) + balanceType
    }

    private fun addHeaderLayout() {
        val headerView = HeaderBillListBinding.inflate(
            LayoutInflater.from(requireContext()),
            binding.llHeader,
            false
        )
        headerBinding = headerView
        binding.llHeader.removeAllViews()
        binding.llHeader.addView(headerView.root)
        headerView.tvDate.text = selectedYearMonth
        headerView.tvType.text = selectedBalanceType?.label.orEmpty()
        headerBinding?.clDate?.onClick {
            if (dateList.isNotEmpty()) {
                StringSelectDialog.newInstance(dateList, selectedYearMonth)
                    .setOnItemSelectedListener { selectedDate ->
                        selectedYearMonth = selectedDate
                        headerBinding?.tvDate?.text = selectedDate
                        refreshList(showRefreshAnimation = false)
                    }
                    .show(parentFragmentManager, StringSelectDialog::class.java.simpleName)
            }
        }
        headerBinding?.clType?.onClick {
            if (balanceTypeList.isNotEmpty()) {
                BalanceTypeSelectDialog.newInstance(balanceTypeList, selectedBalanceType)
                    .setOnItemSelectedListener { selectedType ->
                        selectedBalanceType = selectedType
                        headerBinding?.tvType?.text = selectedType.label.orEmpty()
                        refreshList(showRefreshAnimation = false)
                    }
                    .show(parentFragmentManager, BalanceTypeSelectDialog::class.java.simpleName)
            }
        }
    }


    companion object {
        private const val DEFAULT_TYPE_WITHDRAWAL = "payin_system_subtract"
        private const val DEFAULT_TYPE_ACTIVITY_REWARD = "activity_reward"

        fun newInstance(
            defaultToWithdrawal: Boolean = false,
            defaultToActivity: Boolean = false
        ) = BillsFragment().apply {
            arguments = Bundle().apply {
                putBoolean(Constants.RouterKeys.DEFAULT_TO_WITHDRAWAL, defaultToWithdrawal)
                putBoolean(Constants.RouterKeys.DEFAULT_TO_ACTIVITY, defaultToActivity)
            }
        }
    }
}
