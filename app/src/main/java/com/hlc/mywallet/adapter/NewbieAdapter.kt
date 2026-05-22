package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.NewbieTaskResp
import com.hlc.mywallet.databinding.ItemNewbieBinding

class NewbieAdapter(
    private val onTutorialClick: (NewbieTaskResp) -> Unit,
    private val onDoneClick: (NewbieTaskResp) -> Unit
) : BaseQuickAdapter<NewbieTaskResp, NewbieAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemNewbieBinding = ItemNewbieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: NewbieTaskResp?) {
        item ?: return
        holder.binding.apply {
            item.img?.let {
                ivImg.setImageResource(it)
            }
            val btnColor = if (item.status == TODO) {
                context.getColor(R.color.theme)
            } else {
                context.getColor(R.color.btn_disabled)
            }
            val btnText = if (item.status == TODO) {
                context.getString(R.string.todo)
            } else {
                context.getString(R.string.done)
            }
            btnDone.text = btnText
            btnDone.shapeDrawableBuilder.setSolidColor(btnColor).intoBackground()
            tvTitle.text = item.taskName.orEmpty()
            tvView.onClick {
                onTutorialClick(item)
            }
            btnDone.onClick {
                if (item.status == TODO) {
                    onDoneClick(item)
                }
            }
        }
    }

    companion object {
        const val TODO = "todo"
    }
}
