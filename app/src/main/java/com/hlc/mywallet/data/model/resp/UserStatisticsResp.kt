package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *
 * @author Wade
 *
 * 参数	类型	描述
 * code	number	响应状态码，200 表示成功
 * msg	string	响应消息
 * data	object	个人统计信息
 * data.phone	string	手机号
 * data.userCode	string	用户编码
 * data.username	string	用户名
 * data.balance	string	账户余额（InCoin）
 * data.todayEarnings	string	今日收益（InCoin）
 * data.inTransaction	number	进行中交易数
 * data.todayWithdraw	string	今日提现金额（INR）
 * data.inWithdrawUpiTool	number	UPI 钱包在线数量
 * data.todayOrders	number	今日成功订单数
 * data.todayTotal	string	今日交易总额（INR）
 *
 */
@Parcelize
data class UserStatisticsResp(
    val avatar: String?,
    val balance: String?,
    val inTransaction: Int?,
    val inWithdrawUpiTool: Int?,
    val phone: String?,
    val todayEarnings: String?,
    val todayOrders: Int?,
    val todayTotal: String?,
    val todayWithdraw: String?,
    val userCode: String?,
    val username: String?
): Parcelable