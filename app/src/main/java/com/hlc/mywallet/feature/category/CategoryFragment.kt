package com.hlc.mywallet.feature.category

import com.hlc.lib_base.BaseVbFragment
import com.hlc.mywallet.databinding.FragmentCategoryBinding

class CategoryFragment : BaseVbFragment<FragmentCategoryBinding>() {

    override fun initView() {
        binding.tvTitle.text = "分类"
    }

    companion object {
        fun newInstance() = CategoryFragment()
    }
}
