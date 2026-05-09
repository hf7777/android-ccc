package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/8
 *
 * data.platformOrderNo	string	平台订单号
 * data.toAddress	string	平台 USDT 收款地址
 * data.usdtAmount	string	USDT 充值金额
 * data.expectedBalance	string	预计到账余额（InCoin）
 * data.exchangeRate	string	USDT 兑换汇率
 * data.chainType	string	链类型
 * data.cashierUrl	string	收银台页面 URL
 */
data class UsdtPayResp(
    val cashierUrl: String?,
    val chainType: String?,
    val exchangeRate: String?,
    val expectedBalance: String?,
    val platformOrderNo: String?,
    val toAddress: String?,
    val usdtAmount: String?
)