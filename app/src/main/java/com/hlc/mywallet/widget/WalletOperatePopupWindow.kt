package com.hlc.mywallet.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.PopupWindow
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.Wallet
import com.hlc.mywallet.databinding.PopupWalletOperateBinding

class WalletOperatePopupWindow(
    context: Context,
    private val wallet: Wallet,
    private val onEditUpi: () -> Unit,
    private val onDeAuthorize: () -> Unit,
    private val onBank: () -> Unit = {}
) : PopupWindow(context) {

    private val binding = PopupWalletOperateBinding.inflate(LayoutInflater.from(context))

    init {
        contentView = binding.root
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        elevation = 12f
        setBackgroundDrawable(ColorDrawable(0))

        binding.tvBank.visibleOrGone(wallet.multiCard == 1)

        binding.tvEditUpi.onClick {
            dismiss()
            onEditUpi.invoke()
        }
        binding.tvDeAuthorize.onClick {
            dismiss()
            onDeAuthorize.invoke()
        }
        binding.tvBank.onClick {
            dismiss()
            onBank.invoke()
        }
    }

    fun show(anchor: View) {
        binding.root.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val xOff = anchor.width - binding.root.measuredWidth
        val yOff = anchor.resources.getDimensionPixelSize(R.dimen.wallet_operate_popup_offset_y)
        showAsDropDown(anchor, xOff, yOff)
    }
}
