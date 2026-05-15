package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.formatNumber
import com.hlc.mywallet.data.model.resp.OrderInr
import com.hlc.mywallet.databinding.ItemOrderInrBinding
import okhttp3.internal.addHeaderLenient

class OrderInrAdapter : BaseQuickAdapter<OrderInr, OrderInrAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemOrderInrBinding = ItemOrderInrBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: OrderInr?) {
        item ?: return
        holder.binding.apply {
            tvStatus.text = item.orderStatus
            tvPaymentAmount.text = "${item.orderAmount.formatNumber()}Rs"
            tvReward.text = "+${item.rewardAmount.formatNumber()}"
            tvDeposit.text =  "+${item.balanceChange.formatNumber()}"
            tvTime.text = item.createTime
        }
    }
}
