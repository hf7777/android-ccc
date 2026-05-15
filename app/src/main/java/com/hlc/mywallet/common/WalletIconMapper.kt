package com.hlc.mywallet.common

import androidx.annotation.DrawableRes
import com.hlc.lib_base.R as BaseRes
import com.hlc.mywallet.R

object WalletIconMapper {

    @DrawableRes
    fun getIconRes(channelCode: String?): Int {
        return when (channelCode) {
            "navi" -> R.drawable.ic_pay_navi
            "slice" -> R.drawable.ic_pay_slice
            "airtel" -> R.drawable.ic_pay_airtel
            "mobikwik" -> R.drawable.ic_pay_mobikwik
            "phonepe" -> R.drawable.ic_pay_phonepe
            "phonepe_business" -> R.drawable.ic_pay_phonepebusiness
            "paytm" -> R.drawable.ic_pay_paytm
            "paytm_business" -> R.drawable.ic_pay_paytmbusiness
            "freecharge" -> R.drawable.ic_pay_freecharge
            "tron" -> R.drawable.ic_pay_tron
            else -> BaseRes.drawable.bg_image_placeholder
        }
    }
}
