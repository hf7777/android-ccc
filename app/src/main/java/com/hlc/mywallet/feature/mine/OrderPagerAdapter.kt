package com.hlc.mywallet.feature.mine

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hlc.mywallet.feature.mine.order.InrListFragment
import com.hlc.mywallet.feature.mine.order.UsdtListFragment

class OrderPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        InrListFragment.newInstance(),
        UsdtListFragment.newInstance()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
