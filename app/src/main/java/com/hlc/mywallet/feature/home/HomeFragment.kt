package com.hlc.mywallet.feature.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.addDividerItemDecoration
import com.hlc.lib_base.extension.clearItemDecorations
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.BannerImageAdapter
import com.hlc.mywallet.adapter.TutorialAdapter
import com.hlc.mywallet.data.model.resp.BannersResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import com.hlc.mywallet.databinding.FragmentHomeBinding
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
            }
        }
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
                    Router.navigation(banner.linkValue)
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
        fun newInstance() = HomeFragment()
    }
}
