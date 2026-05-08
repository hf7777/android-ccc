package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/8
 *
 * data.total	number	总记录数
 * data.rows	array	充值订单列表
 * data.rows[].id	string	订单 ID
 * data.rows[].platformOrderNo	string	平台订单号
 * data.rows[].orderType	string	订单类型（upi / bank）
 * data.rows[].orderAmount	number	订单金额（INR）
 * data.rows[].rewardAmount	number	预计佣金（InCoin）
 * data.rows[].balanceChangeAmount	number	预计余额变动 = 订单金额 + 佣金
 * data.rows[].commissionText	string	佣金费率文本（如 3.5%+6）
 */
data class DepositInrResp(
    val rows: List<DepositInr>,
    val total: Int
)

data class DepositInr(
    val balanceChangeAmount: String,
    val commissionText: String,
    val id: String,
    val orderAmount: String,
    val orderType: String,
    val platformOrderNo: String,
    val rewardAmount: String
)