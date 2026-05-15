package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/9
 *
 * data.platformOrderNo	string	平台订单号
 * data.merchantOrderNo	string	商户订单号
 * data.orderStatus	string	订单状态
 * data.grabStatus	string	抢单状态
 * data.orderType	string	订单类型（upi / bank）
 * data.orderAmount	number	订单金额（INR）
 * data.rewardAmount	number	佣金
 * data.balanceChange	number	余额变动
 * data.commissionText	string	佣金费率文本
 * data.ifsc	string	IFSC 银行代码
 * data.accountNo	string	收款账号
 * data.beneName	string	收款人姓名
 * data.paymentMessage	string	付款备注
 * data.grabId	string	抢单记录 ID
 * data.grabExpireTime	string	抢单过期时间
 * data.remainingSeconds	number	剩余秒数（<=0 表示已过期）
 * data.voucherSubmitted	boolean	是否已提交凭证
 * data.channelCode	string	支付渠道编码
 * data.channelName	string	支付渠道名称
 * data.upiId	string	付款方 UPI ID
 * data.tradeCode	string	交易码
 */
data class InrDetailResp(
    val accountNo: String?,
    val balanceChange: String?,
    val beneName: String?,
    val channelCode: String?,
    val commissionText: String?,
    val createTime: String?,
    val grabExpireTime: String?,
    val grabExpireTimestamp: Long?,
    val grabId: String?,
    val grabStatus: String?,
    val ifsc: String?,
    val merchantOrderNo: String?,
    val orderAmount: String?,
    val orderStatus: String?,
    val orderType: String?,
    val paymentMessage: String?,
    val platformOrderNo: String?,
    val remainingSeconds: Int?,
    val rewardAmount: String?,
    val tradeCode: String?,
    val upiId: String?,
    val voucherSubmitted: Boolean
)