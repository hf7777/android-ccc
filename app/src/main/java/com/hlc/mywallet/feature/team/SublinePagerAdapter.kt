package com.hlc.mywallet.feature.team

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SublinePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        SublineFragment.newInstance(SublineLevel.LEVEL_A),
        SublineFragment.newInstance(SublineLevel.LEVEL_B)
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
