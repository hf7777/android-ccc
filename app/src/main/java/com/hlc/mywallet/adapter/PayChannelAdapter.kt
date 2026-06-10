package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hjq.shape.view.ShapeTextView
import com.hlc.mywallet.R
import com.hlc.mywallet.common.WalletIconMapper
import com.hlc.mywallet.data.model.resp.PayChannelResp
import com.hlc.mywallet.databinding.ItemPayChannelBinding

class PayChannelAdapter(private val isAutoBuy: Boolean) : BaseQuickAdapter<PayChannelResp, PayChannelAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemPayChannelBinding = ItemPayChannelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: PayChannelResp?) {
        item ?: return
        holder.binding.apply {
            tvChannel.text = item.channelName
            ivChannel.setImageResource(WalletIconMapper.getIconRes(item.channelCode))
            tvBuy.text = if (isAutoBuy) StringUtils.getString(R.string.auto_buy) else StringUtils.getString(R.string.buy)
            if (isAutoBuy) {
                tvBuy.updateTagStatus(item.autoBuyStatus == STATUS_ENABLE)
            } else {
                tvBuy.updateTagStatus(item.buyStatus == STATUS_ENABLE)
            }
            tvSell.updateTagStatus(item.sellStatus == STATUS_ENABLE)
        }
    }

    private fun ShapeTextView.updateTagStatus(enabled: Boolean) {
        shapeDrawableBuilder.setSolidColor(
            if (enabled) {
                ColorUtils.getColor(R.color.theme)
            } else {
                ColorUtils.getColor(R.color.bg_tag_invalid)
            }
        ).intoBackground()
    }

    companion object {
        private const val STATUS_ENABLE = "enable"
    }
}
