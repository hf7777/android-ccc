package com.hlc.mywallet.feature.deposit

import androidx.fragment.app.viewModels
import com.hlc.mywallet.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.adapter.DepositInrAdapter
import com.hlc.mywallet.data.model.resp.DepositInr
import com.hlc.mywallet.dialog.MyWalletDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class INRFragment : BaseLazyListFragment<DepositInr, DepositInrAdapter>() {

    private val viewModel: DepositViewModel by viewModels()

    private var currentSelectedInr: DepositInr? = null

    override fun onListViewCreated() {
        listAdapter.setOnDebouncedItemClick { adapter, view, i ->
            val item = listAdapter.getItem(i)
            item?.let {
                currentSelectedInr = item
                loadMyWalletAndShowDialog()
            }
        }
        observeMyWallet()
    }

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

    private fun depositInrGrab(platformOrderNo: String, walletId: String) {
        viewModel.depositInrGrab(platformOrderNo, walletId)
    }

    companion object {
        fun newInstance() = INRFragment()
    }
}
