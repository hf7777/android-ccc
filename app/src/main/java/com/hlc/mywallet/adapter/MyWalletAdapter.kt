package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.databinding.ItemMyWalletBinding

class MyWalletAdapter(
    private val payIconProvider: (String?) -> Int
) : BaseQuickAdapter<MyWalletResp, MyWalletAdapter.VH>() {

    private var selectedPosition = 0

    class VH(
        parent: ViewGroup,
        val binding: ItemMyWalletBinding = ItemMyWalletBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: MyWalletResp?) {
        item ?: return
        holder.binding.apply {
            ivPay.setImageResource(payIconProvider(item.channelCode))
            tvName.text = item.channelName ?: ""
            tvUpi.text = item.upi ?: ""
            cbPay.setImageResource(
                if (position == selectedPosition) {
                    R.drawable.cb_checked
                } else {
                    R.drawable.cb_unchecked
                }
            )
            root.setOnClickListener {
                updateSelection(holder.bindingAdapterPosition)
            }
            cbPay.setOnClickListener {
                updateSelection(holder.bindingAdapterPosition)
            }
        }
    }

    fun submitWallets(wallets: List<MyWalletResp>) {
        selectedPosition = 0
        submitList(wallets)
    }

    fun getSelectedWallet(): MyWalletResp? {
        return items.getOrNull(selectedPosition)
    }

    private fun updateSelection(position: Int) {
        if (position == RecyclerView.NO_POSITION || position == selectedPosition) {
            return
        }
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }
}
