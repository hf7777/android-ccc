package com.hlc.mywallet.data.model.req

/**
 * @author Wade
 * @since 2026/6/3
 */
data class BankInfoReq(
    val accountName: String,
    val accountNo: String,
    val beneName: String,
    val ifsc: String
)
