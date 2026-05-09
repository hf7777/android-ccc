package com.hlc.mywallet.feature.deposit

import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.KeyboardUtils
import com.hlc.lib_base.BaseVbFragment
import com.hlc.lib_base.extension.optimizeSwipeSensitivity
import com.hlc.mywallet.adapter.DepositTabAdapter
import com.hlc.mywallet.databinding.FragmentDepositBinding
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

class DepositFragment : BaseVbFragment<FragmentDepositBinding>() {

    private val titles = listOf("INR", "USDT")
    private lateinit var pagerAdapter: DepositPagerAdapter

    override fun initView() {
        setupViewPager()
        setupMagicIndicator()
    }

    private fun setupViewPager() {
        pagerAdapter = DepositPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.optimizeSwipeSensitivity()
    }

    private fun setupMagicIndicator() {
        val commonNavigator = CommonNavigator(requireContext())
        commonNavigator.isAdjustMode = true // 设置为平均分布模式
        commonNavigator.adapter = DepositTabAdapter(
            titles = titles,
            onTabClick = { index ->
                binding.viewPager.currentItem = index
            }
        )
        binding.magicIndicator.navigator = commonNavigator

        // 绑定 ViewPager2
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.magicIndicator.onPageSelected(position)
                KeyboardUtils.hideSoftInput(requireActivity())
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                binding.magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                binding.magicIndicator.onPageScrollStateChanged(state)
            }
        })
    }

    companion object {
        fun newInstance() = DepositFragment()
    }
}
