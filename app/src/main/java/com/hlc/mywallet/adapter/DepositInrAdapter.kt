package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.setDrawablePadding
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.DepositInr
import com.hlc.mywallet.databinding.ItemDepositInrBinding

class DepositInrAdapter : BaseQuickAdapter<DepositInr, DepositInrAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemDepositInrBinding = ItemDepositInrBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: DepositInr?) {
        item ?: return
        holder.binding.apply {
            "${item.orderAmount.formatNumber()}${StringUtils.getString(R.string.price_unit_rs)}".also { tvPrice.text = it }
            tvBank.text = item.orderType.uppercase()
            tvReward.text = item.rewardAmount.formatNumber()
            tvReward.setDrawablePadding(rightResId = R.drawable.ic_coin, rightPadding = 5.dp, drawableWidth = 18.dp, drawableHeight = 18.dp)
            tvCommission.text = item.commissionText
            tvInCoin.text = item.balanceChangeAmount.formatNumber()
            tvInCoin.setDrawablePadding(rightResId = R.drawable.ic_coin, rightPadding = 5.dp, drawableWidth = 18.dp, drawableHeight = 18.dp)
        }
    }
}
