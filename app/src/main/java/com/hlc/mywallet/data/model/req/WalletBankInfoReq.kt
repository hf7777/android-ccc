package com.hlc.mywallet.data.model.req

/**
 * @author Wade
 * @since 2026/6/3
 *
 *| `walletId` | string | Yes | 钱包ID |
 * | `accountNo` | string | Yes | 银行账号 |
 * | `bankName` | string | No | 银行名称 |
 * | `upiPin` | string | No | UPI PIN |
 */
data class WalletBankInfoReq(
    val walletId: String,
    val accountNo: String,
    val bankName: String,
    val upiPin: String
)
