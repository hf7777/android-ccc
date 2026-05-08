package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.loadRounded
import com.hlc.mywallet.data.model.resp.TutorialResp
import com.hlc.mywallet.databinding.ItemTutorialBinding

class TutorialAdapter : BaseQuickAdapter<TutorialResp, TutorialAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemTutorialBinding = ItemTutorialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: TutorialResp?) {
        item ?: return
        holder.binding.apply {
            tvDate.text = item.createTime ?: ""
            tvTitle.text = item.title ?: ""
            ivCover.loadRounded(item.coverImage ?: "", 5)
        }
    }
}