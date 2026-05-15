package com.hlc.mywallet.feature.mine.order

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.buildAdapterHelper
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.OrderInrAdapter
import com.hlc.mywallet.common.DateMonthUtils
import com.hlc.mywallet.data.model.resp.OrderInr
import com.hlc.mywallet.databinding.HeaderOrderListBinding
import com.hlc.mywallet.dialog.StringSelectDialog
import com.hlc.mywallet.feature.deposit.PaymentDetailActivity
import com.hlc.mywallet.feature.mine.MineViewModel
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InrListFragment : BaseLazyListFragment<OrderInr, OrderInrAdapter>() {

    private val viewModel: MineViewModel by viewModels()
    private val dateList by lazy { DateMonthUtils.getRecentYearMonths() }
    private var listHelper: QuickAdapterHelper? = null
    private var selectedYearMonth = DateMonthUtils.getCurrentYearMonth()
    private var headerBinding: HeaderOrderListBinding? = null

    override fun initView() {
        super.initView()
        refreshLayout.setBackgroundResource(R.color.bg_deposit)
        listAdapter.setOnDebouncedItemClick { adapter, view, i ->
            val id = listAdapter.getItem(i)?.id
            id?.let {
                Router.navigation(Routes.PAYMENT_DETAIL)
                    .with(PaymentDetailActivity.KEY_GRAB_RECORD_ID, it)
                    .navigation(this)
            }
        }
    }

    override fun onListViewCreated() {
        addHeaderLayout()
        listHelper = listAdapter.buildAdapterHelper(
            headerSize = 10.dp,
            footerSize = 20.dp
        )
        recyclerView.adapter = listHelper?.adapter
    }

    override fun createAdapter(): OrderInrAdapter {
        return OrderInrAdapter()
    }

    override fun requestListData(page: Int) {
        viewModel.getOrderInrList(page, 20, selectedYearMonth)
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
        viewModel.orderInrListFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onSuccess = { data ->
                onListLoadSuccess(data.rows, false)
            },
            onError = { errorMsg ->
                onListLoadError(errorMsg)
            }
        )
    }

    private fun addHeaderLayout() {
        val headerView = HeaderOrderListBinding.inflate(
            LayoutInflater.from(requireContext()),
            binding.llHeader,
            false
        )
        headerBinding = headerView
        binding.llHeader.removeAllViews()
        binding.llHeader.addView(headerView.root)
        headerView.tvDate.text = selectedYearMonth
        headerView.clDate.onClick {
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
    }

    companion object {
        fun newInstance() = InrListFragment()
    }
}
