package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.formatNumber
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.ItemWithdrawRecordBinding
import com.hlc.mywallet.data.model.resp.WithdrawRecord

class WithdrawRecordAdapter : BaseQuickAdapter<WithdrawRecord, WithdrawRecordAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemWithdrawRecordBinding = ItemWithdrawRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: WithdrawRecord?) {
        item ?: return
        holder.binding.apply {
            tvAmount.text = "${StringUtils.getString(R.string.price_symbol)}${item.orderAmount.orEmpty().formatNumber()}"
            tvDate.text = item.createTime.orEmpty()
            tvStatus.text = item.status.orEmpty()
        }
    }
}
