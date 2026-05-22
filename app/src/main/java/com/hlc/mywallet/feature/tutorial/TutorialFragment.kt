package com.hlc.mywallet.feature.tutorial

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.TutorialAdapter
import com.hlc.mywallet.data.model.resp.TutorialResp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TutorialFragment : BaseLazyListFragment<TutorialResp, TutorialAdapter>() {

    private val viewModel: TutorialViewModel by viewModels()

    override fun initView() {
        super.initView()
        listAdapter.setOnDebouncedItemClick { _, _, i ->
            listAdapter.getItem(i)?.let { tutorial ->
                TutorialDetailActivity.start(requireContext(), tutorial)
            }
        }
    }

    override fun createAdapter(): TutorialAdapter {
        return TutorialAdapter()
    }

    override fun requestListData(page: Int) {
        viewModel.getTutorials()
    }

    override fun createLayoutManager() = LinearLayoutManager(requireContext())

    override fun createItemDecoration() =
        SpaceItemDecoration.Builder()
            .dividerColor(ColorUtils.getColor(R.color.home_tutorial_divider), 1.dp)
            .build()

    override fun enableRefresh(): Boolean = true

    override fun enableLoadMore(): Boolean = false

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tutorialResultFlow.collect { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            showPageLoading()
                        }
                        is ApiResult.Success -> {
                            onListLoadSuccess(result.data, hasMore = false)
                            showPageContent()
                        }

                        is ApiResult.Error -> {
                            showPageContent()
                            onListLoadError(result.exception.message)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = TutorialFragment()
    }
}
