package com.hlc.mywallet.feature.wallet

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PayChannelPagerAdapter(activity: FragmentActivity, isAutoBuy: Boolean) : FragmentStateAdapter(activity) {

    companion object {
        private const val STATUS_ENABLE = "enable"
    }

    private val fragments = if (isAutoBuy) {
        listOf(PayChannelFragment.newInstance(true, buyStatus = "", autoBuyStatus = STATUS_ENABLE))
    } else {
        listOf(
            PayChannelFragment.newInstance(false, buyStatus = STATUS_ENABLE, autoBuyStatus = ""),
            PayChannelFragment.newInstance(false, sellStatus = STATUS_ENABLE)
        )
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
