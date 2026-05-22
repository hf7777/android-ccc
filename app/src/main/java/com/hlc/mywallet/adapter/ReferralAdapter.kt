package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ClipboardUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hjq.toast.Toaster
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.Subordinate
import com.hlc.mywallet.databinding.ItemReferralBinding

class ReferralAdapter(
    private val onClaimClick: (Subordinate) -> Unit
) : BaseQuickAdapter<Subordinate, ReferralAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemReferralBinding = ItemReferralBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: Subordinate?) {
        item ?: return
        holder.binding.apply {
            val account = item.phone.orEmpty()
            val state = resolveState(item)
            val rewardText = item.rewardAmount?.toString().formatNumber()

            tvTitle.text = item.subordinateUsername.orEmpty()
            tvAccount.text = account

            if (account.isNotBlank()) {
                ivCopy.onClick {
                    ClipboardUtils.copyText(account)
                    Toaster.show(R.string.copy_success)
                }
            }

            val statusColor = when (state) {
                ReferralItemState.UNCOMPLETED -> R.color.text_color_gray
                ReferralItemState.CLAIMABLE -> R.color.status_green
                ReferralItemState.RECEIVED -> R.color.theme
            }
            val statusText = when (state) {
                ReferralItemState.UNCOMPLETED -> context.getString(R.string.newbie_task_not_completed)
                ReferralItemState.CLAIMABLE -> context.getString(R.string.newbie_task_completed)
                ReferralItemState.RECEIVED -> context.getString(R.string.reward_received)
            }
            val buttonColor = when (state) {
                ReferralItemState.UNCOMPLETED -> R.color.btn_disabled
                ReferralItemState.CLAIMABLE -> R.color.theme
                ReferralItemState.RECEIVED -> R.color.status_green
            }
            val buttonText = when (state) {
                ReferralItemState.RECEIVED -> {
                    context.getString(R.string.received_amount, rewardText)
                }
                else -> {
                    context.getString(R.string.receive_amount, rewardText)
                }
            }

            tvStatusDesc.text = statusText
            tvStatusDesc.setTextColor(ContextCompat.getColor(context, statusColor))
            btnDone.text = buttonText
            btnDone.shapeDrawableBuilder
                .setSolidColor(ContextCompat.getColor(context, buttonColor))
                .intoBackground()
            btnDone.onClick {
                if (state == ReferralItemState.CLAIMABLE) {
                    onClaimClick(item)
                }
            }
        }
    }

    private fun resolveState(item: Subordinate): ReferralItemState {
        return when (item.status?.lowercase()) {
            STATUS_CLAIMED -> ReferralItemState.RECEIVED
            STATUS_DONE -> ReferralItemState.CLAIMABLE
            else -> ReferralItemState.UNCOMPLETED
        }
    }

    private enum class ReferralItemState {
        UNCOMPLETED,
        CLAIMABLE,
        RECEIVED
    }

    companion object {
        private const val STATUS_DONE = "done"
        private const val STATUS_CLAIMED = "claimed"
    }
}
