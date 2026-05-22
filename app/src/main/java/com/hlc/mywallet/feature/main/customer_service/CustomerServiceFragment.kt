package com.hlc.mywallet.feature.main.customer_service

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter4.QuickAdapterHelper
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.buildAdapterHelper
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.CustomerServiceAdapter
import com.hlc.mywallet.data.model.resp.CustomerServiceResp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomerServiceFragment :
    BaseLazyListFragment<CustomerServiceResp, CustomerServiceAdapter>() {

    private val viewModel: CustomerServiceViewModel by viewModels()

    private var listHelper: QuickAdapterHelper? = null

    override fun createAdapter(): CustomerServiceAdapter {
        return CustomerServiceAdapter(onContactClick = ::openTelegramLink)
    }

    override fun requestListData(page: Int) {
        viewModel.getCustomerService()
    }

    override fun createLayoutManager() = LinearLayoutManager(requireContext())

    override fun createItemDecoration() =
        SpaceItemDecoration.Builder()
            .verticalSpacing(10)
            .build()

    override fun enableRefresh(): Boolean = true

    override fun enableLoadMore(): Boolean = false

    override fun onListViewCreated() {
        listHelper = listAdapter.buildAdapterHelper(
            headerSize = 10.dp,
            footerSize = 20.dp
        )
        recyclerView.adapter = listHelper?.adapter
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.customerServiceState.collect { result ->
                    when (result) {
                        is ApiResult.Loading -> Unit
                        is ApiResult.Success -> {
                            onListLoadSuccess(result.data, hasMore = false)
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

    private fun openTelegramLink(item: CustomerServiceResp) {
        val link = item.telegramLink?.trim().orEmpty()
        if (link.isBlank()) {
            showError(getString(R.string.error_unknown))
            return
        }
        runCatching {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        }.onFailure {
            showError(it.message ?: getString(R.string.error_unknown))
        }
    }

    companion object {
        fun newInstance() = CustomerServiceFragment()
    }
}
