package com.hlc.mywallet.feature.team

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.QuickAdapterHelper
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.buildAdapterHelper
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.SublineAdapter
import com.hlc.mywallet.data.model.resp.SublineResp
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Wade
 * @since 2026/5/15
 */
@AndroidEntryPoint
class SublineFragment : BaseLazyListFragment<SublineResp, SublineAdapter>() {

    private val viewModel: TeamViewModel by viewModels()
    private var level: String = SublineLevel.LEVEL_A
    private var listHelper: QuickAdapterHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        level = arguments?.getString(KEY_LEVEL).orEmpty().ifEmpty { SublineLevel.LEVEL_A }
    }

    override fun initView() {
        super.initView()
        refreshLayout.setBackgroundResource(R.color.bg_0f_theme)
    }

    override fun onListViewCreated() {
        listHelper = listAdapter.buildAdapterHelper(
            headerSize = 10.dp,
            footerSize = 20.dp
        )
        recyclerView.adapter = listHelper?.adapter
    }

    override fun createAdapter(): SublineAdapter {
        return SublineAdapter()
    }

    override fun requestListData(page: Int) {
        viewModel.getSublineList(level)
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
        viewModel.sublineListState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onSuccess = { data ->
                onListLoadSuccess(data, false)
            },
            onError = { errorMsg ->
                onListLoadError(errorMsg)
            }
        )
    }

    companion object {
        private const val KEY_LEVEL = "level"

        fun newInstance(level: String) = SublineFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_LEVEL, level)
            }
        }
    }
}
