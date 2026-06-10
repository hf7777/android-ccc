package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.loadRounded
import com.hlc.lib_base.extension.visible
import com.hlc.mywallet.R
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
            tvDate.text = item.updateTime ?: ""
            tvTitle.text = item.title ?: ""
            ivCover.loadRounded(item.coverImage ?: R.drawable.ic_logo, 5, scaleType = ImageView.ScaleType.CENTER_INSIDE)
            ivPlayMask.visibility = if (item.videoUrl.isNullOrBlank()) {
                android.view.View.GONE
            } else {
                android.view.View.VISIBLE
            }
        }
    }
}
