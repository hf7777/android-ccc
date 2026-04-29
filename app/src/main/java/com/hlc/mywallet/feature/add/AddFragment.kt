package com.hlc.mywallet.feature.add

import com.hlc.lib_base.BaseVbFragment
import com.hlc.mywallet.databinding.FragmentAddBinding

class AddFragment : BaseVbFragment<FragmentAddBinding>() {

    override fun initView() {
        binding.tvTitle.text = "记账"
    }

    companion object {
        fun newInstance() = AddFragment()
    }
}
