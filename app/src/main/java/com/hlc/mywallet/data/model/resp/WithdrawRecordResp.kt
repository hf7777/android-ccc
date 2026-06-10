package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Wade
 * @since 2026/5/14
 */
data class WithdrawRecordResp(
    val rows: List<WithdrawRecord>,
    val total: Int
)

@Parcelize
data class WithdrawRecord(
    val beneName: String?,
    val platformOrderNo: String?,
    val orderAmount: String?,
    val createTime: String?,
    val accountNo: String?,
    val ifsc: String?,
    val utr: String?,
    val successTime: String?,
    val status: String?
) : Parcelable
