package com.hlc.mywallet.feature.mine.withdraw

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.buildAdapterHelper
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.mywallet.adapter.WithdrawRecordAdapter
import com.hlc.mywallet.data.model.resp.WithdrawRecord
import com.hlc.mywallet.dialog.WithdrawRecordDetailDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawRecordsFragment : BaseLazyListFragment<WithdrawRecord, WithdrawRecordAdapter>() {

    private val viewModel: WithdrawRecordsViewModel by viewModels()
    private var listHelper: QuickAdapterHelper? = null

    override fun onListViewCreated() {
        listHelper = listAdapter.buildAdapterHelper(
            headerSize = 10.dp,
            footerSize = 20.dp
        ) {
            setTrailingLoadStateAdapter(createTrailingLoadMoreListener())
        }
        recyclerView.adapter = listHelper?.adapter
        quickAdapterHelper = listHelper
        listHelper?.trailingLoadStateAdapter?.apply {
            preloadSize = loadMorePreloadThreshold()
            isAutoLoadMore = true
        }
        listHelper?.trailingLoadState = LoadState.None

        listAdapter.setOnDebouncedItemClick { _, _, position ->
            listAdapter.getItem(position)?.let { record ->
                showWithdrawRecordDetailDialog(record)
            }
        }
    }

    override fun createAdapter(): WithdrawRecordAdapter = WithdrawRecordAdapter()

    override fun requestListData(page: Int) {
        viewModel.getWithdrawRecords(page, WithdrawRecordsViewModel.PAGE_SIZE)
    }

    override fun createLayoutManager() = LinearLayoutManager(requireContext())

    override fun createItemDecoration() = SpaceItemDecoration.Builder()
        .verticalSpacing(10.dp)
        .build()

    override fun enableRefresh(): Boolean = true

    override fun enableLoadMore(): Boolean = true

    override fun observeData() {
        viewModel.withdrawRecordsFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {},
            onSuccess = { data ->
                val newSize = if (isRefreshingData) {
                    data.rows.size
                } else {
                    currentList.size + data.rows.size
                }
                val hasMore = newSize < data.total
                onListLoadSuccess(data.rows, hasMore)
            },
            onError = { errorMsg ->
                onListLoadError(errorMsg)
            }
        )
    }

    private fun showWithdrawRecordDetailDialog(record: WithdrawRecord) {
        WithdrawRecordDetailDialog.newInstance(record)
            .show(childFragmentManager, WithdrawRecordDetailDialog::class.java.simpleName)
    }

    companion object {
        fun newInstance() = WithdrawRecordsFragment()
    }
}
