package com.hlc.mywallet.feature.home

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.addDividerItemDecoration
import com.hlc.lib_base.extension.clearItemDecorations
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.setDrawablePadding
import com.hlc.lib_base.extension.setOnClick
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.common.AppEvent
import com.hlc.mywallet.common.AppEventBus
import com.hlc.mywallet.adapter.BannerImageAdapter
import com.hlc.mywallet.adapter.TutorialAdapter
import com.hlc.mywallet.data.model.resp.BannersResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import com.hlc.mywallet.databinding.FragmentHomeBinding
import com.hlc.mywallet.extension.startBreathingScaleAnimation
import com.hlc.mywallet.extension.stopBreathingScaleAnimation
import com.hlc.mywallet.feature.main.MainActivity
import com.hlc.mywallet.feature.tutorial.TutorialDetailActivity
import com.hlc.mywallet.router.Routes
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseVbFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    private val tutorialAdapter by lazy {
        TutorialAdapter()
    }

    override fun initView() {
        binding.tvBalance.setDrawablePadding(leftResId = R.drawable.ic_coin, leftPadding = 4.dp, drawableWidth = 18.dp, drawableHeight = 18.dp)
        binding.tvBalanceNewbie.setDrawablePadding(leftResId = R.drawable.ic_coin, leftPadding = 4.dp, drawableWidth = 18.dp, drawableHeight = 18.dp)
        binding.refreshLayout.apply {
            setColorSchemeResources(R.color.theme)
            setOnRefreshListener {
                viewModel.loadData(isRefresh = true)
            }
        }

        binding.banner.apply {
            addBannerLifecycleObserver(this@HomeFragment)
            indicator = CircleIndicator(requireContext())
            setLoopTime(3000)
        }

        binding.tvLearnMore.onClick {
            navigation(Routes.TUTORIAL_LIST)
        }

        binding.llWithdraw.onClick {
            (activity as? MainActivity)?.selectTab(2)
        }

        setOnClick(binding.llDeposit, binding.clPrice, binding.tvUsdtGo, binding.tvRupeeGo, binding.clRupee) {
            (activity as? MainActivity)?.selectTab(1)
        }

        tutorialAdapter.setOnDebouncedItemClick { _, _, i ->
            tutorialAdapter.getItem(i)?.let { tutorial ->
                TutorialDetailActivity.start(requireContext(), tutorial)
            }
        }

        binding.llBalanceNewbie.onClick {
            navigation(Routes.BONUS_CENTER)
        }

        binding.ivCs.onClick {
            viewModel.loadCustomerServiceUrl()
        }

        setupTutorialList()
        viewModel.loadData()
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect(::render)
                }
                launch {
                    viewModel.eventFlow.collect(::handleEvent)
                }
                launch {
                    AppEventBus.flow<AppEvent.NewbieSummaryUpdated>().collect(::renderNewbieBalanceUi)
                }
            }
        }

        viewModel.customerServiceUrlFlow.collectWithError(
            lifecycleOwner = viewLifecycleOwner,
            onLoading = { showLoading() },
            onSuccess = { data ->
                hideLoading()
                val url = data.url?.trim().orEmpty()
                if (url.isBlank()) {
                    Toaster.show(getString(R.string.error_unknown))
                    return@collectWithError
                }

                runCatching {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }.onFailure {
                    Toaster.show(getString(R.string.error_unknown))
                }
            },
            onError = { message ->
                hideLoading()
                Toaster.show(message)
            }
        )
    }

    private fun renderNewbieBalanceUi(event: AppEvent.NewbieSummaryUpdated) {
        if (event.isCompleted) {
            binding.tvBalance.visible()
            binding.llBalanceNewbie.gone()
            binding.tvCompleteTip.stopBreathingScaleAnimation()
        } else {
            binding.tvBalance.gone()
            binding.llBalanceNewbie.visible()
            binding.tvBalanceNewbie.text = event.totalReward.toString().formatNumber()
            binding.tvCompleteTip.startBreathingScaleAnimation()
        }
    }

    override fun onDestroyView() {
        binding.tvCompleteTip.stopBreathingScaleAnimation()
        super.onDestroyView()
    }

    private fun render(state: HomeUiState) {
        binding.refreshLayout.isRefreshing = state.isRefreshing

        if (state.isLoading) {
            showPageLoading()
            return
        }

        showPageContent()
        initBanner(state.banners)
        state.priceInfo?.let(::initPriceUi)
        tutorialAdapter.submitList(state.tutorials)
    }

    private fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ShowError -> {
                if (viewModel.uiState.value.hasContent()) {
                    Toaster.show(event.message)
                } else {
                    showPageError(
                        message = event.message,
                        onActionClick = { viewModel.loadData() }
                    )
                }
            }
        }
    }

    private fun initBanner(data: List<BannersResp>) {
        if (data.isEmpty()) return

        binding.banner.apply {
            setAdapter(BannerImageAdapter(data))
            setOnBannerListener { _, position ->
                val banner = data[position]
                if (!banner.linkValue.isNullOrEmpty()) {
                    if (banner.linkValue == Routes.FRAGMENT_TEAM) {
                        (activity as? MainActivity)?.selectTab(TEAM_TAB_INDEX)
                    } else {
                        Router.navigation(banner.linkValue).navigation(this@HomeFragment)
                    }
                }
            }
        }
    }

    private fun initPriceUi(priceInfo: PriceInfoResp) {
        binding.tvBalance.text = priceInfo.totalBalance ?: ""
        binding.tvMarketPrice.text = priceInfo.marketUsdtRate ?: ""
        binding.tvOurPrice.text = priceInfo.platUsdtRate ?: ""
        if (priceInfo.inrFeeRate != null && priceInfo.inrFeeSingle != null) {
            binding.tvReward.text =
                StringUtils.getString(
                    R.string.up_to_reward,
                    priceInfo.inrFeeRate,
                    priceInfo.inrFeeSingle
                )
        }
    }

    private fun setupTutorialList() {
        binding.rvTutorial.apply {
            layoutManager = object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            adapter = tutorialAdapter
            clearItemDecorations()
            addDividerItemDecoration(ColorUtils.getColor(R.color.home_tutorial_divider), 1.dp)
        }
    }

    companion object {
        private const val TEAM_TAB_INDEX = 3

        fun newInstance() = HomeFragment()
    }
}
