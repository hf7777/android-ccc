package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/7
 *
 * data.totalCommission	string	总佣金
 * data.todayDepositCount	number	今日充值单数
 * data.todayCommission	string	今日佣金
 * data.yesterdayDepositCount	number	昨日充值单数
 * data.yesterdayCommission	string	昨日佣金
 * data.totalDepositCount	number	累计充值单数
 * data.totalSublineCount	number	累计下线人数
 * data.inviteLink	string	邀请链接
 * data.parentCommissionRate	string	上级佣金比例
 * data.grandparentCommissionRate	string	上上级佣金比例
 */
data class TeamStatisticsResp(
    val grandparentCommissionRate: String?,
    val inviteLink: String?,
    val parentCommissionRate: String?,
    val todayCommission: String?,
    val todayDepositCount: Int?,
    val totalCommission: String?,
    val totalDepositCount: Int?,
    val totalSublineCount: Int?,
    val yesterdayCommission: String?,
    val yesterdayDepositCount: Int?,
    val commissionModelImageUrl: String?
)