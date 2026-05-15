package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.load
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.extension.visibleOrGone
import com.hjq.shape.layout.ShapeConstraintLayout
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.common.WalletIconMapper
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.Wallet
import com.hlc.mywallet.databinding.ItemWalletBinding

class WalletAdapter(
    private val onOperates: (anchor: View, wallet: Wallet) -> Unit,
    private val onReLink: (wallet: Wallet) -> Unit,
    private val onStatus: (wallet: Wallet) -> Unit
) : BaseQuickAdapter<Wallet, WalletAdapter.VH>() {

    private var onSellSwitchChangedListener: ((position: Int, item: Wallet, isChecked: Boolean) -> Unit)? = null

    class VH(
        parent: ViewGroup,
        val binding: ItemWalletBinding = ItemWalletBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: Wallet?) {
        item ?: return
        holder.binding.apply {
            ivIcon.load(WalletIconMapper.getIconRes(item.channelCode))
            tvName.text = item.channelName ?: ""
            tvEmail.text = item.upi ?: ""

            if (item.onlineStatus == ONLINE_STATUS_ENABLE) {
                clContainer.updateSolidColor(ColorUtils.getColor(R.color.bg_wallet_available))
                tvStatus.text = context.getString(R.string.available)
                tvTips.gone()
                msSwitcher.isEnabled = true
                btnOperate.text = StringUtils.getString(R.string.operates)
                btnOperate.shapeDrawableBuilder.setSolidColor(ColorUtils.getColor(R.color.black)).intoBackground()
            } else {
                clContainer.updateSolidColor(ColorUtils.getColor(R.color.bg_wallet_pause))
                tvStatus.text = context.getString(R.string.pause)
                tvTips.visible()
                msSwitcher.isEnabled = false
                btnOperate.text = StringUtils.getString(R.string.re_link)
                btnOperate.shapeDrawableBuilder.setSolidColor(ColorUtils.getColor(R.color.theme)).intoBackground()
            }
            msSwitcher.setOnCheckedChangeListener(null)
            groupStartSelling.visibleOrGone(item.channelSellStatus == CHANNEL_SELL_STATUS_OPEN)
            msSwitcher.isChecked = item.sellStatus == SELL_STATUS_OPEN
            msSwitcher.setOnCheckedChangeListener { _, isChecked ->
                val adapterPosition = holder.bindingAdapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSellSwitchChangedListener?.invoke(adapterPosition, item, isChecked)
                }
            }

            btnOperate.onClick {
                if (item.onlineStatus == ONLINE_STATUS_ENABLE) {
                    onOperates.invoke(btnOperate, item)
                } else {
                    onReLink.invoke(item)
                }
            }

            btnStatus.onClick {
                onStatus.invoke(item)
            }
        }
    }

    fun setOnSellSwitchChangedListener(listener: (position: Int, item: Wallet, isChecked: Boolean) -> Unit) {
        onSellSwitchChangedListener = listener
    }

    fun revertSellSwitch(position: Int) {
        notifyItemChanged(position)
    }

    private fun ShapeConstraintLayout.updateSolidColor(color: Int) {
        shapeDrawableBuilder.setSolidColor(color).intoBackground()
    }

    companion object {
        private const val ONLINE_STATUS_ENABLE = "Y"
        private const val SELL_STATUS_OPEN = "enable"
        private const val CHANNEL_SELL_STATUS_OPEN = "enable"
    }
}
