package com.hlc.mywallet.feature.mine

import com.hlc.lib_base.BaseVbFragment
import com.hlc.mywallet.databinding.FragmentMineBinding

class MineFragment : BaseVbFragment<FragmentMineBinding>() {

    override fun initView() {
        binding.tvTitle.text = "我的"
    }

    companion object {
        fun newInstance() = MineFragment()
    }
}
