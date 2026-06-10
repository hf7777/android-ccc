package com.hlc.mywallet.feature.wallet

import android.view.LayoutInflater
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.setDrawablePadding
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showConfirmDialog
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.WalletAdapter
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.Wallet
import com.hlc.mywallet.data.model.resp.WalletBankInfoResp
import com.hlc.mywallet.dialog.WalletBankCardDetailDialog
import com.hlc.mywallet.dialog.WalletBankCardDialog
import com.hlc.mywallet.dialog.WalletStatusDialog
import com.hlc.mywallet.databinding.HeaderWalletBinding
import com.hlc.mywallet.dialog.ChoosePlanDialog
import com.hlc.mywallet.dialog.OrderDetailsDialog
import com.hlc.mywallet.dialog.StatusDescDialog
import com.hlc.mywallet.feature.wallet.bean.PayChannelSetupArgs
import com.hlc.mywallet.router.Routes
import com.hlc.mywallet.widget.WalletOperatePopupWindow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletFragment : BaseLazyListFragment<Wallet, WalletAdapter>() {

    private val viewModel: WalletViewModel by viewModels()
    private var pendingSwitchPosition = RecyclerView.NO_POSITION
    private var pendingRelinkWallet: Wallet? = null
    private var pendingBankCardWallet: Wallet? = null

    override fun createAdapter(): WalletAdapter {
        return WalletAdapter(
            onOperates = { anchor, wallet ->
                showWalletOperateMenu(anchor, wallet)
            },
            onReLink = { wallet ->
                relink(wallet)
            },
            onStatus = { wallet ->
                showWalletStatusDialog(wallet)
            }
        )
    }

    override fun requestListData(page: Int) {
        viewModel.getWalletList(page = page, pageSize = PAGE_SIZE)
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext())
    }

    override fun createItemDecoration() = SpaceItemDecoration.Builder()
        .verticalSpacing(10.dp)
        .build()

    override fun enableRefresh(): Boolean = true

    override fun enableLoadMore(): Boolean = true

    override fun getFirstPage(): Int = FIRST_PAGE

    override fun onListViewCreated() {
        super.onListViewCreated()
        addHeaderLayout()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppEventBus.flow<AppEvent.WalletRefreshRequested>().collect {
                    refreshList(showRefreshAnimation = false)
                }
            }
        }
        listAdapter.setOnSellSwitchChangedListener { position, item, isChecked ->
            pendingSwitchPosition = position
            if (isChecked) {
                viewModel.startSelling(item.phone ?: "", item.channelCode ?: "")
            } else {
                viewModel.closeSelling(item.phone ?: "", item.channelCode ?: "")
            }
        }
    }

    override fun observeData() {
        viewModel.walletListState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onSuccess = { data ->
                val hasMore = currentList.size + data.rows.size < data.total
                onListLoadSuccess(data.rows, hasMore)
            },
            onError = { errorMsg ->
                onListLoadError(errorMsg)
            }
        )
        viewModel.walletSellState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = { showLoading() },
            onSuccess = {
                hideLoading()
                clearPendingSwitchState()
                refreshList(showRefreshAnimation = false)
            },
            onError = {
                hideLoading()
                rollbackPendingSwitch()
            }
        )
        viewModel.deAuthorizeFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = {
                hideLoading()
                refreshList(showRefreshAnimation = false)
            },
            onError = {
                hideLoading()
            }
        )
        viewModel.relinkFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = { needRefresh ->
                hideLoading()
                val wallet = pendingRelinkWallet
                pendingRelinkWallet = null
                if (needRefresh) {
                    refreshList(showRefreshAnimation = false)
                } else if (wallet != null) {
                    goPayChannelSetup(wallet)
                }
            },
            onError = {
                hideLoading()
                pendingRelinkWallet = null
            }
        )
        viewModel.walletVersionFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = { version ->
                hideLoading()
                handleWalletVersion(version)
            },
            onError = {
                hideLoading()
            }
        )
        viewModel.walletBankCardFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = { showLoading() },
            onSuccess = {
                hideLoading()
                dismissWalletBankCardDialog()
                dismissWalletBankCardDetailDialog()
                Toaster.show(getString(R.string.save_successfully))
                refreshList(showRefreshAnimation = false)
            },
            onError = { message ->
                hideLoading()
                Toaster.show(message)
            }
        )
        viewModel.walletBankCardListFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = { showLoading() },
            onSuccess = { bankCardList ->
                hideLoading()
                val wallet = pendingBankCardWallet ?: return@collectWithError
                val bankInfo = bankCardList.firstOrNull()
                if (bankInfo != null) {
                    showWalletBankCardDetailDialog(wallet, bankInfo)
                } else {
                    showAddWalletBankCardDialog(wallet)
                }
            },
            onError = { message ->
                hideLoading()
                Toaster.show(message)
            }
        )

    }

    private fun relink(wallet: Wallet) {
        val phone = wallet.phone.orEmpty()
        val channelCode = wallet.channelCode.orEmpty()
        if (phone.isEmpty() || channelCode.isEmpty()) return
        pendingRelinkWallet = wallet
        viewModel.relink(phone, channelCode)
    }

    private fun goPayChannelSetup(wallet: Wallet) {
        Router.navigation(Routes.PAY_CHANNEL_SETUP)
            .withBundle(Bundle().apply {
                putParcelable(
                    Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS,
                    PayChannelSetupArgs.fromWallet(wallet, wallet.walletVersion == WalletAdapter.WALLET_VERSION_2)
                )
            })
            .navigation(this)
    }

    private fun addHeaderLayout() {
        val headerView = HeaderWalletBinding.inflate(
            LayoutInflater.from(requireContext()),
            binding.llHeader,
            false
        )
        binding.llHeader.removeAllViews()
        binding.llHeader.addView(headerView.root)
        headerView.tvDesc.setDrawablePadding(
            leftResId = R.drawable.ic_question,
            leftPadding = 4.dp,
            drawableWidth = 18.dp,
            drawableHeight = 18.dp
        )
        headerView.tvDesc.onClick {
            StatusDescDialog.newInstance()
                .show(childFragmentManager, StatusDescDialog::class.java.simpleName)
        }
        headerView.btnAdd.onClick {
            viewModel.walletVersion()
        }
    }

    private fun handleWalletVersion(version: String) {
        when (version) {
            "" -> {
                ChoosePlanDialog.newInstance()
                    .setOnConfirmListener { plan ->
                        val isAutoBuy = plan == ChoosePlanDialog.Plan.A
                        goPayChannel(isAutoBuy)
                    }
                    .show(childFragmentManager, ChoosePlanDialog::class.java.simpleName)
            }
            "1.0" -> {
                goPayChannel(false)
            }
            "2.0" -> {
                goPayChannel(true)
            }
            else -> {
                // 未知版本保持兼容，默认走非自动买入
                goPayChannel(false)
            }
        }
    }

    private fun goPayChannel(isAutoBuy: Boolean) {
        Router.navigation(Routes.PAY_CHANNEL)
            .with(Constants.RouterKeys.IS_AUTO_BUY, isAutoBuy)
            .navigation(this)
    }

    private fun showWalletOperateMenu(anchor: View, wallet: Wallet) {
        WalletOperatePopupWindow(requireContext(),
            wallet,
            onEditUpi = {
                handleEditUpi(wallet)
            },
            onDeAuthorize = {
                handleDeAuthorize(wallet)
            },
            onBank = {
                if (wallet.bankCardAccountNo.isNullOrEmpty()) {
                    showAddWalletBankCardDialog(wallet)
                } else {
                    loadWalletBankCardList(wallet)
                }
            }
        ).show(anchor)
    }

    private fun loadWalletBankCardList(wallet: Wallet) {
        val walletId = wallet.id.orEmpty()
        if (walletId.isEmpty()) return
        pendingBankCardWallet = wallet
        viewModel.getWalletBankCardList(walletId)
    }

    private fun showWalletBankCardDetailDialog(wallet: Wallet, bankInfo: WalletBankInfoResp) {
        WalletBankCardDetailDialog.newInstance(wallet.upi, bankInfo)
            .setOnUpdateListener { info ->
                dismissWalletBankCardDetailDialog()
                showEditWalletBankCardDialog(wallet, info)
            }
            .show(childFragmentManager, WalletBankCardDetailDialog::class.java.simpleName)
    }

    private fun showAddWalletBankCardDialog(wallet: Wallet) {
        val walletId = wallet.id.orEmpty()
        if (walletId.isEmpty()) {
            return
        }
        WalletBankCardDialog.newInstance(walletId, wallet.upi)
            .setOnSubmitListener { req ->
                viewModel.checkUpiPinAndAddWalletBankCard(req, false)
            }
            .show(childFragmentManager, WalletBankCardDialog::class.java.simpleName)
    }

    private fun showEditWalletBankCardDialog(wallet: Wallet, bankInfo: WalletBankInfoResp) {
        val walletId = wallet.id.orEmpty()
        if (walletId.isEmpty()) return
        WalletBankCardDialog.newInstanceForEdit(walletId, wallet.upi, bankInfo)
            .setOnSubmitListener { req ->
                viewModel.checkUpiPinAndAddWalletBankCard(req, true)
            }
            .show(childFragmentManager, WalletBankCardDialog::class.java.simpleName)
    }

    private fun dismissWalletBankCardDialog() {
        val dialog = childFragmentManager
            .findFragmentByTag(WalletBankCardDialog::class.java.simpleName) as? WalletBankCardDialog
        dialog?.dismissAllowingStateLoss()
    }

    private fun dismissWalletBankCardDetailDialog() {
        val dialog = childFragmentManager
            .findFragmentByTag(WalletBankCardDetailDialog::class.java.simpleName) as? WalletBankCardDetailDialog
        dialog?.dismissAllowingStateLoss()
    }

    private fun handleEditUpi(wallet: Wallet) {
        Router.navigation(Routes.EDIT_UPI)
            .withBundle(Bundle().apply {
                putParcelable(Constants.RouterKeys.WALLET, wallet)
            })
            .navigation(this)
    }

    private fun handleDeAuthorize(wallet: Wallet) {
        val phone = wallet.phone.orEmpty()
        val channelCode = wallet.channelCode.orEmpty()
        if (phone.isNotEmpty() && channelCode.isNotEmpty()) {
            showConfirmDialog(content = getString(R.string.are_you_sure_you_want_to_de_authorize_this_wallet)) {
                viewModel.deAuthorize(phone, channelCode)
            }
        }
    }

    private fun showWalletStatusDialog(wallet: Wallet) {
        WalletStatusDialog.newInstance(wallet)
            .show(childFragmentManager, WalletStatusDialog::class.java.simpleName)
    }

    private fun rollbackPendingSwitch() {
        if (pendingSwitchPosition != RecyclerView.NO_POSITION) {
            listAdapter.revertSellSwitch(pendingSwitchPosition)
        }
        clearPendingSwitchState()
    }

    private fun clearPendingSwitchState() {
        pendingSwitchPosition = RecyclerView.NO_POSITION
    }

    companion object {
        private const val FIRST_PAGE = 0
        private const val PAGE_SIZE = 20

        fun newInstance() = WalletFragment()
    }
}
