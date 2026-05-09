package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/9
 *
 * data.total	number	总记录数
 * data.rows	array	抢单记录列表
 * data.rows[].id	string	抢单记录 ID
 * data.rows[].platformOrderNo	string	平台订单号
 * data.rows[].orderStatus	string	订单状态
 * data.rows[].orderAmount	number	订单金额（INR）
 * data.rows[].rewardAmount	number	佣金
 * data.rows[].balanceChange	number	余额变动
 * data.rows[].createTime	string	创建时间
 */
data class OrderInrResp(
    val rows: List<Row>,
    val total: Int
)

data class Row(
    val balanceChange: String,
    val createTime: String,
    val id: String,
    val orderAmount: String,
    val orderStatus: String,
    val platformOrderNo: String,
    val rewardAmount: String
)