package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.ClipboardUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hjq.toast.Toaster
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.setDrawablePadding
import com.hlc.lib_base.extension.visible
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.Bill
import com.hlc.mywallet.databinding.ItemBillsBinding

class BillsAdapter : BaseQuickAdapter<Bill, BillsAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemBillsBinding = ItemBillsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: Bill?) {
        item ?: return
        holder.binding.apply {
            tvTitle.text = item.typeText
            tvDate.text = item.createTime
            tvAmount.text = buildAmountText(item.direction, item.amount.orEmpty())
            tvPoint.text =
                context.getString(R.string.a_points_after_balance, item.afterBalance.formatNumber())

            tvUpi.setDrawablePadding(rightResId = R.drawable.ic_copy, rightPadding = 3.dp, drawableWidth = 16.dp, drawableHeight = 16.dp)
            if (item.upi.isNullOrEmpty()) {
                tvUpi.gone()
            } else {
                tvUpi.visible()
                tvUpi.text = context.getString(R.string.upi_format, item.upi)
                tvUpi.onClick {
                    ClipboardUtils.copyText(item.upi)
                    Toaster.show(R.string.copy_success)
                }
            }

            tvUtr.setDrawablePadding(rightResId = R.drawable.ic_copy, rightPadding = 3.dp, drawableWidth = 16.dp, drawableHeight = 16.dp)
            if (item.utr.isNullOrEmpty()) {
                tvUtr.gone()
            } else {
                tvUtr.visible()
                tvUtr.text = context.getString(R.string.utr_format, item.utr)
                tvUtr.onClick {
                    ClipboardUtils.copyText(item.utr)
                    Toaster.show(R.string.copy_success)
                }
            }
        }
    }

    private fun buildAmountText(direction: String?, amount: String): String {
        val formattedAmount = amount.formatNumber()
        return if (direction.equals(DIRECTION_OUT, ignoreCase = true)) {
            "-$formattedAmount"
        } else {
            "+$formattedAmount"
        }
    }

    companion object {
        private const val DIRECTION_OUT = "out"
    }
}
