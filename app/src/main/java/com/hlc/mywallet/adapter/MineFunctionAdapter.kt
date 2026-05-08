package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.mywallet.databinding.ItemMineFunctionBinding
import com.hlc.mywallet.feature.mine.bean.MineFunction

class MineFunctionAdapter : BaseQuickAdapter<MineFunction, MineFunctionAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemMineFunctionBinding = ItemMineFunctionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: MineFunction?) {
        item ?: return
        holder.binding.apply {
            ivFunc.setImageResource(item.imgRes)
            tvFunc.text = item.title
        }
    }
}
