package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.loadCircle
import com.hlc.mywallet.data.model.resp.SublineResp
import com.hlc.mywallet.databinding.ItemSublineBinding

/**
 * 下级列表适配器。
 * 展示头像、用户名、注册时间以及贡献佣金。
 */
class SublineAdapter : BaseQuickAdapter<SublineResp, SublineAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemSublineBinding = ItemSublineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: SublineResp?) {
        item ?: return
        holder.binding.apply {
            ivAvatar.loadCircle(item.avatar)
            tvTitle.text = item.username
            tvDate.text = item.createTime
            tvAmount.text = buildCommissionText(item.commission)
        }
    }

    private fun buildCommissionText(commission: String): String {
        val formattedCommission = commission.formatNumber()
        return if (formattedCommission.startsWith("-")) {
            formattedCommission
        } else {
            "+$formattedCommission"
        }
    }
}
