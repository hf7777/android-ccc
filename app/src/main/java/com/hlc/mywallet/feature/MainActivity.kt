package com.hlc.mywallet.feature

import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hlc.lib_base.BaseVbActivity
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ActivityMainBinding
import com.hlc.mywallet.feature.add.AddFragment
import com.hlc.mywallet.feature.category.CategoryFragment
import com.hlc.mywallet.feature.chart.ChartFragment
import com.hlc.mywallet.feature.home.HomeFragment
import com.hlc.mywallet.feature.mine.MineFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseVbActivity<ActivityMainBinding>() {

    private val fragments = mutableListOf<Fragment>()
    private var currentPosition = 0

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarDarkIcon(true)
            navigationBarColorInt(ColorUtils.getColor(R.color.white))
            fitsSystemWindows(true)
        }
    }

    override fun initView() {
        initFragments()
        setupBottomNavigation()
    }

    private fun initFragments() {
        fragments.add(HomeFragment.newInstance())
        fragments.add(CategoryFragment.newInstance())
        fragments.add(AddFragment.newInstance())
        fragments.add(ChartFragment.newInstance())
        fragments.add(MineFragment.newInstance())

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragments[0])
            .commit()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnTabSelectedListener { position ->
            switchFragment(position)
        }
    }

    private fun switchFragment(position: Int) {
        if (position == currentPosition) return

        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(fragments[currentPosition])

        if (fragments[position].isAdded) {
            transaction.show(fragments[position])
        } else {
            transaction.add(R.id.fragment_container, fragments[position])
        }

        transaction.commit()
        currentPosition = position
    }
}
