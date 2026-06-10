package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Wade
 * @since 2026/6/8
 */
@Parcelize
data class WalletBankInfoResp(
    val id: Int,
    val antId: String?,
    val username: String?,
    val walletId: String?,
    val phone: String?,
    val accountNo: String?,
    val bankName: String?,
    val upiPin: String?,
    val upiPinStatus: String?,
    val accountNoStatus: String?,
    val autoBuyStatus: String?,
    val status: String?,
    val errorMsg: String?
) : Parcelable
