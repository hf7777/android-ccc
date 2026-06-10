package com.hlc.mywallet.feature.wallet

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.hlc.lib_base.BaseLazyListFragment
import com.hlc.lib_base.extension.buildAdapterHelper
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showConfirmDialog
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.PayChannelAdapter
import com.hlc.mywallet.common.Constants
import com.hlc.mywallet.data.model.resp.CheckBindingResp
import com.hlc.mywallet.data.model.resp.PayChannelResp
import com.hlc.mywallet.feature.wallet.bean.PayChannelSetupArgs
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayChannelFragment : BaseLazyListFragment<PayChannelResp, PayChannelAdapter>() {

    private val viewModel: WalletViewModel by viewModels()
    private var sellStatus: String = ""
    private var buyStatus: String = ""
    private var autoBuyStatus: String = ""

    private var isAutoBuyVersion: Boolean = true

    private var listHelper: QuickAdapterHelper? = null
    private var currentChannel: PayChannelResp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAutoBuyVersion = arguments?.getBoolean(KEY_IS_AUTO_BUY) ?: true
        sellStatus = arguments?.getString(KEY_SELL) ?: ""
        buyStatus = arguments?.getString(KEY_BUY) ?: ""
        autoBuyStatus = arguments?.getString(KEY_AUTO_BUY) ?: ""
    }

    override fun initView() {
        super.initView()
        refreshLayout.setBackgroundColor(resources.getColor(R.color.bg_0f_theme))
        listAdapter.setOnDebouncedItemClick { adapter, view, i ->
            val item = listAdapter.getItem(i)
            this.currentChannel = item
            item?.let {
                viewModel.checkBinding()
            }
        }
    }

    override fun onListViewCreated() {
        listHelper = listAdapter.buildAdapterHelper(
            headerSize = 10.dp,
            footerSize = 20.dp
        )
        recyclerView.adapter = listHelper?.adapter
    }

    override fun createAdapter(): PayChannelAdapter {
        return PayChannelAdapter(isAutoBuyVersion)
    }

    override fun requestListData(page: Int) {
        viewModel.payChannelList(sellStatus, buyStatus, autoBuyStatus)
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
        viewModel.payChannelListState.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onSuccess = { data ->
                onListLoadSuccess(data, false)
            },
            onError = { errorMsg ->
                onListLoadError(errorMsg)
            }
        )

        viewModel.checkBindingFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = {
                showLoading()
            },
            onSuccess = { data ->
                checkBinding(data)
                hideLoading()
            },
            onError = { errorMsg ->
                hideLoading()
                onListLoadError(errorMsg)
            }
        )
    }

    private fun checkBinding(data: CheckBindingResp) {
        if (!data.tgBound) {
            // 去绑定Tg
            showConfirmDialog(content = StringUtils.getString(R.string.please_bind_telegram), showCancelButton = false) {
                navigation(Routes.BIND_TG)
            }
        } else if (!data.pinSet) {
            // 去设置PIN
            showConfirmDialog(content = getString(R.string.to_secure_your_funds), showCancelButton = false) {
                navigation(Routes.PIN)
            }
        } else {
            // 都绑定了
            currentChannel?.let {
                Router.navigation(Routes.PAY_CHANNEL_SETUP)
                    .withBundle(Bundle().apply {
                        putParcelable(
                            Constants.RouterKeys.PAY_CHANNEL_SETUP_ARGS,
                            PayChannelSetupArgs.fromPayChannel(it, isAutoBuyVersion)
                        )
                    })
                    .navigation(this)
            }
        }
    }

    companion object {
        private const val KEY_SELL = "sell"
        private const val KEY_BUY = "buy"
        private const val KEY_AUTO_BUY = "auto_buy"

        private const val KEY_IS_AUTO_BUY = "is_auto_buy"

        fun newInstance(isAutoBuyVersion: Boolean = true, sellStatus: String = "", buyStatus: String = "", autoBuyStatus: String = "") = PayChannelFragment().apply {
            arguments = Bundle().apply {
                putBoolean(KEY_IS_AUTO_BUY, isAutoBuyVersion)
                putString(KEY_SELL, sellStatus)
                putString(KEY_BUY, buyStatus)
                putString(KEY_AUTO_BUY, autoBuyStatus)
            }
        }
    }
}
