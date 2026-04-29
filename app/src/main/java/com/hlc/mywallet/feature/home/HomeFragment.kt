package com.hlc.mywallet.feature.home

import com.hlc.lib_base.BaseVbFragment
import com.hlc.mywallet.databinding.FragmentHomeBinding

class HomeFragment : BaseVbFragment<FragmentHomeBinding>() {

    override fun initView() {
        binding.tvTitle.text = "首页"
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
