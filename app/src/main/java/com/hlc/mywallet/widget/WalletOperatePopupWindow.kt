package com.hlc.mywallet.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.PopupWindow
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R

class WalletOperatePopupWindow(
    context: Context,
    private val onEditUpi: () -> Unit,
    private val onDeAuthorize: () -> Unit
) : PopupWindow(context) {

    private val contentViewInternal: View = LayoutInflater.from(context)
        .inflate(R.layout.popup_wallet_operate, null, false)

    init {
        contentView = contentViewInternal
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        elevation = 12f
        setBackgroundDrawable(ColorDrawable(0))

        contentViewInternal.findViewById<View>(R.id.tv_edit_upi).onClick {
            dismiss()
            onEditUpi.invoke()
        }
        contentViewInternal.findViewById<View>(R.id.tv_de_authorize).onClick {
            dismiss()
            onDeAuthorize.invoke()
        }
    }

    fun show(anchor: View) {
        contentViewInternal.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val xOff = anchor.width - contentViewInternal.measuredWidth
        val yOff = anchor.resources.getDimensionPixelSize(R.dimen.wallet_operate_popup_offset_y)
        showAsDropDown(anchor, xOff, yOff)
    }
}
