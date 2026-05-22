package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Wade
 * @since 2026/5/9
 */
data class OrderUsdtResp(
    val rows: List<OrderUsdt>,
    val total: Int
)

@Parcelize
data class OrderUsdt(
    val balanceChange: String?,
    val cashierUrl: String?,
    val createTime: String?,
    val exchangeRate: String?,
    val platformOrderNo: String?,
    val status: String?,
    val toAddress: String?,
    val txHash: String?,
    val usdtAmount: String?
) : Parcelable
