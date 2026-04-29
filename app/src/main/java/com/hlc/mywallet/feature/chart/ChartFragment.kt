package com.hlc.mywallet.feature.chart

import com.hlc.lib_base.BaseVbFragment
import com.hlc.mywallet.databinding.FragmentChartBinding

class ChartFragment : BaseVbFragment<FragmentChartBinding>() {

    override fun initView() {
        binding.tvTitle.text = "图表"
    }

    companion object {
        fun newInstance() = ChartFragment()
    }
}
