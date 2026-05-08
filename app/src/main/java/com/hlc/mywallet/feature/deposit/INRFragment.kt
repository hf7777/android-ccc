package com.hlc.mywallet.feature.deposit

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.adapter.DepositInrAdapter
import com.hlc.mywallet.data.model.resp.DepositInr
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class INRFragment : BaseLazyListFragment<DepositInr, DepositInrAdapter>() {

    private val viewModel: DepositViewModel by viewModels()

    override fun createAdapter(): DepositInrAdapter {
        return DepositInrAdapter()
    }

    override fun requestListData(page: Int) {
        viewModel.getDepositInrList(page = page)
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext())
    }

    override fun createItemDecoration() = null

    override fun enableRefresh(): Boolean = true

    override fun enableLoadMore(): Boolean = true

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.depositInrResultFlow.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val hasMore = currentList.size + result.data.rows.size < result.data.total
                            onListLoadSuccess(result.data.rows, hasMore)
                        }

                        is ApiResult.Error -> {
                            onListLoadError(result.exception.message)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = INRFragment()
    }
}
