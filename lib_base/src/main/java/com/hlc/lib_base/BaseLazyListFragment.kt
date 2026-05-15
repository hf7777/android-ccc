package com.hlc.lib_base

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.hlc.lib_base.databinding.FragmentBaseLazyListBinding
import com.hlc.lib_base.widget.hideLoading

abstract class BaseLazyListFragment<T : Any, A : BaseQuickAdapter<T, out RecyclerView.ViewHolder>> :
    BaseLazyFragment<FragmentBaseLazyListBinding>() {

    private val dataList = mutableListOf<T>()

    protected lateinit var listAdapter: A
        private set

    protected var quickAdapterHelper: QuickAdapterHelper? = null
        private set

    protected val recyclerView: RecyclerView
        get() = binding.recyclerView

    protected val refreshLayout
        get() = binding.refreshLayout

    protected val currentList: List<T>
        get() = dataList

    protected var currentPage: Int = getFirstPage()
        private set

    protected var hasMoreData: Boolean = true
        private set

    protected var isRefreshingData: Boolean = false
        private set

    protected var isLoadingMoreData: Boolean = false
        private set

    private var refreshEnabled = false
    private var loadMoreEnabled = false

    override fun initView() {
        super.initView()
        binding.refreshLayout.apply {
            setColorSchemeResources(R.color.theme)
        }
        listAdapter = createAdapter()
        refreshEnabled = enableRefresh()
        loadMoreEnabled = enableLoadMore()
        setupRefreshLayout()
        setupRecyclerView()
        onListViewCreated()
    }

    override fun loadLazyData() {
        startFirstLoad()
    }

    protected abstract fun createAdapter(): A

    protected abstract fun requestListData(page: Int)

    protected open fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireContext())
    }

    protected open fun createItemDecoration(): RecyclerView.ItemDecoration? = null

    protected open fun onListViewCreated() = Unit

    protected open fun enableRefresh(): Boolean = true

    protected open fun enableLoadMore(): Boolean = false

    protected open fun getFirstPage(): Int = 1

    protected open fun loadMorePreloadThreshold(): Int = 1

    protected open fun isLoadEndDisplay(): Boolean = true

    protected open fun onListDataChanged(data: List<T>) {
        listAdapter.submitList(data)
    }

    override fun getPageStateContainer(view: View): ViewGroup? {
        return binding.flContent
    }

    protected fun refreshList(showRefreshAnimation: Boolean = true) {
        if (isRefreshingData || isLoadingMoreData) {
            return
        }
        currentPage = getFirstPage()
        isRefreshingData = true
        quickAdapterHelper?.trailingLoadState = LoadState.None
        if (refreshEnabled && showRefreshAnimation) {
            refreshLayout.isRefreshing = true
        } else if (currentList.isEmpty()) {
            showPageLoading()
        }
        requestListData(currentPage)
    }

    protected fun onListLoadSuccess(data: List<T>, hasMore: Boolean = data.isNotEmpty()) {
        if (!isRefreshingData && !isLoadingMoreData) {
            return
        }

        if (isRefreshingData) {
            dataList.clear()
            dataList.addAll(data)
        } else {
            currentPage += 1
            dataList.addAll(data)
        }

        hasMoreData = hasMore
        onListDataChanged(dataList.toList())
        finishLoadSuccess(hasMore)
    }

    protected fun onListLoadError(message: String? = null) {
        val isLoadMoreRequest = isLoadingMoreData
        val shouldShowPageError = !isLoadMoreRequest && currentList.isEmpty()
        finishLoadingState()
        if (isLoadMoreRequest) {
            quickAdapterHelper?.trailingLoadState = LoadState.Error(Throwable(message ?: "Load more failed"))
        } else if (shouldShowPageError) {
            showPageError(
                message = getString(R.string.page_error),
                onActionClick = {
                    refreshList(showRefreshAnimation = false)
                }
            )
        } else {
            updateLoadMoreState()
        }
        if (!message.isNullOrEmpty() && !shouldShowPageError) {
            showError(message)
        }
    }

    protected fun setRefreshEnabled(enabled: Boolean) {
        refreshEnabled = enabled
        refreshLayout.isEnabled = enabled
    }

    protected fun setLoadMoreEnabled(enabled: Boolean) {
        loadMoreEnabled = enabled
        hasMoreData = enabled
        updateLoadMoreState()
    }

    protected fun resetListState() {
        dataList.clear()
        currentPage = getFirstPage()
        hasMoreData = loadMoreEnabled
        onListDataChanged(emptyList())
        updateLoadMoreState()
    }

    private fun setupRefreshLayout() {
        refreshLayout.isEnabled = refreshEnabled
        refreshLayout.setOnRefreshListener {
            refreshList()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = createLayoutManager()
            createItemDecoration()?.let(::addItemDecoration)
            adapter = buildRecyclerViewAdapter()
        }
    }

    private fun buildRecyclerViewAdapter(): RecyclerView.Adapter<*> {
        if (!loadMoreEnabled) {
            return listAdapter
        }

        return QuickAdapterHelper.Builder(listAdapter)
            .setTrailingLoadStateAdapter(createTrailingLoadMoreListener())
            .build()
            .also { helper ->
                quickAdapterHelper = helper
                helper.trailingLoadStateAdapter?.apply {
                    preloadSize = loadMorePreloadThreshold()
                    isAutoLoadMore = true
                }
                helper.trailingLoadState = LoadState.None
            }
            .adapter
    }

    private fun createTrailingLoadMoreListener(): TrailingLoadStateAdapter.OnTrailingListener {
        return object : TrailingLoadStateAdapter.OnTrailingListener {
            override fun onLoad() {
                startLoadMore()
            }

            override fun onFailRetry() {
                startLoadMore()
            }

            override fun isAllowLoading(): Boolean {
                return !isRefreshingData && !isLoadingMoreData && loadMoreEnabled && hasMoreData
            }
        }
    }

    private fun startFirstLoad() {
        currentPage = getFirstPage()
        isRefreshingData = true
        quickAdapterHelper?.trailingLoadState = LoadState.None
        showPageLoading()
        requestListData(currentPage)
    }

    private fun startLoadMore() {
        if (isRefreshingData || isLoadingMoreData || !loadMoreEnabled || !hasMoreData) {
            return
        }
        isLoadingMoreData = true
        requestListData(currentPage + 1)
    }

    private fun finishLoadSuccess(hasMore: Boolean) {
        finishLoadingState()
        hasMoreData = hasMore
        if (currentList.isEmpty()) {
            showPageEmpty()
        } else {
            showPageContent()
        }
        updateLoadMoreState()
        quickAdapterHelper?.trailingLoadStateAdapter?.checkDisableLoadMoreIfNotFullPage()
    }

    private fun finishLoadingState() {
        isRefreshingData = false
        isLoadingMoreData = false
        refreshLayout.isRefreshing = false
        hideLoading()
    }

    private fun updateLoadMoreState() {
        if (!loadMoreEnabled) {
            return
        }
        quickAdapterHelper?.trailingLoadState = when {
            isRefreshingData -> LoadState.None
            hasMoreData -> LoadState.NotLoading.Incomplete
            isLoadEndDisplay() -> LoadState.NotLoading.Complete
            else -> LoadState.None
        }
    }
}
