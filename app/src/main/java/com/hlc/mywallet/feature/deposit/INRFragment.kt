package com.hlc.mywallet.feature.deposit

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.KeyboardUtils
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.setDrawablePadding
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.DepositInrAdapter
import com.hlc.mywallet.data.model.resp.DepositInr
import com.hlc.mywallet.databinding.HeaderDepositInrBinding
import com.hlc.mywallet.dialog.MyWalletDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class INRFragment : BaseLazyListFragment<DepositInr, DepositInrAdapter>() {

    private val viewModel: DepositViewModel by viewModels()

    private var currentSelectedInr: DepositInr? = null

    private var headerBinding: HeaderDepositInrBinding? = null
    private var selectedOrderAmountSort: String? = null

    override fun onListViewCreated() {
        addHeaderLayout()
        setupKeyboardDismissOnScroll()
        listAdapter.setOnDebouncedItemClick { adapter, view, i ->
            val item = listAdapter.getItem(i)
            item?.let {
                currentSelectedInr = item
                loadMyWalletAndShowDialog()
            }
        }
        observeMyWallet()
    }

    private fun setupKeyboardDismissOnScroll() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    headerBinding?.etMin?.clearFocus()
                    headerBinding?.etMax?.clearFocus()
                    KeyboardUtils.hideSoftInput(requireActivity())
                }
            }
        })
    }

    override fun createAdapter(): DepositInrAdapter {
        return DepositInrAdapter()
    }

    override fun requestListData(page: Int) {
        viewModel.getDepositInrList(
            page = page,
            minAmount = headerBinding?.etMin?.text?.toString()?.trim()?.takeIf { it.isNotEmpty() },
            maxAmount = headerBinding?.etMax?.text?.toString()?.trim()?.takeIf { it.isNotEmpty() },
            orderAmountSort = selectedOrderAmountSort
        )
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext())
    }

    override fun createItemDecoration() = null

    override fun enableRefresh(): Boolean = true

    override fun enableLoadMore(): Boolean = true

    override fun observeData() {
        viewModel.depositInrResultFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onSuccess = { data ->
                val hasMore = currentList.size + data.rows.size < data.total
                onListLoadSuccess(data.rows, hasMore)
            },
            onError = { errorMsg ->
                onListLoadError(errorMsg)
            }
        )
    }

    private fun loadMyWalletAndShowDialog() {
        viewModel.getMyWallet()
    }

    private fun observeMyWallet() {
        viewModel.myWalletResultFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = { showLoading() },
            onSuccess = { wallets ->
                hideLoading()
                if (wallets.isNotEmpty()) {
                    MyWalletDialog
                        .newInstance(wallets)
                        .setOnConfirmListener { wallet ->
                            if (currentSelectedInr != null && !wallet.id.isNullOrEmpty()) {
                                depositInrGrab(currentSelectedInr?.platformOrderNo ?: "", wallet.id)
                            }
                        }
                        .show(requireActivity().supportFragmentManager, "MyWalletDialog")
                }
            },
            onError = { errorMsg ->
                hideLoading()
            }
        )

        viewModel.inrGrabFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = { showLoading() },
            onSuccess = { grabRecordId ->
                hideLoading()
                PaymentDetailActivity.start(requireContext(), grabRecordId)
            },
            onError = { errorMsg ->
                hideLoading()
            }
        )
    }

    private fun addHeaderLayout() {
        val headerView = HeaderDepositInrBinding.inflate(
            LayoutInflater.from(requireContext()),
            binding.llHeader,
            false
        )
        headerBinding = headerView
        binding.llHeader.removeAllViews()
        binding.llHeader.addView(headerView.root)

        headerView.btnGo.onClick {
            filterList()
        }
        headerView.btnReset.onClick {
            headerView.etMin.text?.clear()
            headerView.etMax.text?.clear()
            selectedOrderAmountSort = null
            updateSortSelectedState()
            filterList()
        }
        headerView.tvAsc.onClick {
            selectedOrderAmountSort = if (selectedOrderAmountSort == SORT_ASC) null else SORT_ASC
            updateSortSelectedState()
            filterList()
        }
        headerView.tvDesc.onClick {
            selectedOrderAmountSort = if (selectedOrderAmountSort == SORT_DESC) null else SORT_DESC
            updateSortSelectedState()
            filterList()
        }
        updateSortSelectedState()
    }

    private fun filterList() {
        showLoading()
        refreshList(showRefreshAnimation = false)
    }

    private fun updateSortSelectedState() {
        headerBinding?.apply {
            tvAsc.isSelected = selectedOrderAmountSort == SORT_ASC
            tvDesc.isSelected = selectedOrderAmountSort == SORT_DESC
            tvAsc.setDrawablePadding(
                rightResId = if (tvAsc.isSelected) {
                    R.drawable.ic_arrow_l_up_selected
                } else {
                    R.drawable.ic_arrow_l_up
                },
                drawableWidth = 8.dp,
                drawableHeight = 8.dp
            )
            tvDesc.setDrawablePadding(
                rightResId = if (tvDesc.isSelected) {
                    R.drawable.ic_arrow_l_down_selected
                } else {
                    R.drawable.ic_arrow_l_down
                },
                drawableWidth = 8.dp,
                drawableHeight = 8.dp
            )
        }
    }

    private fun depositInrGrab(platformOrderNo: String, walletId: String) {
        viewModel.depositInrGrab(platformOrderNo, walletId)
    }

    companion object {
        private const val SORT_ASC = "asc"
        private const val SORT_DESC = "desc"

        fun newInstance() = INRFragment()
    }
}
