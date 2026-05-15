package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.formatNumber
import com.hlc.mywallet.data.model.resp.OrderUsdt
import com.hlc.mywallet.databinding.ItemOrderUsdtBinding

class OrderUsdtAdapter : BaseQuickAdapter<OrderUsdt, OrderUsdtAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemOrderUsdtBinding = ItemOrderUsdtBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: OrderUsdt?) {
        item ?: return
        holder.binding.apply {
            tvOrderNo.text = item.platformOrderNo
            tvStatus.text = item.status
            tvPaymentAmount.text = "${item.usdtAmount.formatNumber()} USDT"
            tvDeposit.text = "+${item.balanceChange.formatNumber()}"
            tvTime.text = item.createTime
        }
    }
}
