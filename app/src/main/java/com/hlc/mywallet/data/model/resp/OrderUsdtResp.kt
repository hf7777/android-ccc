package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/9
 *
 * data.rows[].platformOrderNo	string	平台订单号
 * data.rows[].createTime	string	创建时间
 * data.rows[].status	string	订单状态
 * data.rows[].usdtAmount	number	USDT 金额
 * data.rows[].balanceChange	number	余额变动（InCoin）
 * data.rows[].exchangeRate	number	兑换汇率
 * data.rows[].cashierUrl	string	收银台 URL
 * data.rows[].toAddress	string	充值地址
 * data.rows[].txHash	string	交易哈希
 */
data class OrderUsdtResp(
    val rows: List<OrderUsdt>,
    val total: Int
)

data class OrderUsdt(
    val balanceChange: String,
    val cashierUrl: String,
    val createTime: String,
    val exchangeRate: String,
    val platformOrderNo: String,
    val status: String,
    val toAddress: String,
    val usdtAmount: String
)