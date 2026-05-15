package com.hlc.mywallet.feature.mine.order

import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.optimizeSwipeSensitivity
import com.hlc.mywallet.R
import com.hlc.mywallet.adapter.DepositTabAdapter
import com.hlc.mywallet.databinding.ActivityOrderBinding
import com.hlc.mywallet.feature.mine.OrderPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

@AndroidEntryPoint
class OrderActivity : BaseVbActivity<ActivityOrderBinding>() {

    private val titles = listOf("INR", "USDT")
    private lateinit var pagerAdapter: OrderPagerAdapter

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarColorInt(ColorUtils.getColor(R.color.white))
            navigationBarDarkIcon(true)
            fitsSystemWindows(true)
        }
    }

    override fun useBaseTitleBar(): Boolean = true

    override fun getBaseTitleBarTitle(): String = getString(R.string.order)

    override fun initView() {
        setupViewPager()
        setupMagicIndicator()
    }

    private fun setupViewPager() {
        pagerAdapter = OrderPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.optimizeSwipeSensitivity()
    }

    private fun setupMagicIndicator() {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = DepositTabAdapter(
            titles = titles,
            onTabClick = { index ->
                binding.viewPager.currentItem = index
            }
        )
        binding.magicIndicator.navigator = commonNavigator
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.magicIndicator.onPageSelected(position)
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
}
