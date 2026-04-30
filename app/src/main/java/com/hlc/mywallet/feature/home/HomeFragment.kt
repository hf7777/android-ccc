package com.hlc.mywallet.feature.home

import androidx.fragment.app.viewModels
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.router.Router
import com.hlc.lib_base.widget.hideLoading
import com.hlc.mywallet.adapter.BannerImageAdapter
import com.hlc.mywallet.data.model.resp.BannersResp
import com.hlc.mywallet.databinding.FragmentHomeBinding
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseVbFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    override fun initView() {

        // 配置 Banner
        binding.banner.apply {
            addBannerLifecycleObserver(this@HomeFragment)
            indicator = CircleIndicator(requireContext())
            setLoopTime(3000)
        }
        
        viewModel.getBanners()
        viewModel.getPriceInfo()
    }

    private fun initBanner(data: List<BannersResp>) {
        if (data.isNotEmpty()) {
            binding.banner.apply {
                setAdapter(BannerImageAdapter(data))
                setOnBannerListener { bannerData, position ->
                    val banner = data[position]
                    if (!banner.linkValue.isNullOrEmpty()) {
                        Router.navigation(banner.linkValue)
                    }
                }
            }
        }
    }


    override fun observeData() {
        viewModel.bannerUiState.collectWithError(
            lifecycleOwner = this,
            onSuccess = { data ->
                initBanner(data)
            },
        )

        viewModel.priceUiState.collectWithError(
            lifecycleOwner = this,
            onLoading = {
                showLoading()
            },
            onSuccess = { data ->

            },
            onError = {
                hideLoading()
            },
        )
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
