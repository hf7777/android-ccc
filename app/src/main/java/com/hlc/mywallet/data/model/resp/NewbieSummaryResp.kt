package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/19
 *
 * | isCompleted | number | 是否已完成(0-未完成,1-已完成) |
 * | totalReward | BigDecimal | 新手任务总奖励金额（A Points） |
 * | isRewarded | number | 是否领取奖励(0-未领取,1-已领取) |
 */
class NewbieSummaryResp(
    val isCompleted: Int,
    val totalReward: Double,
    val isRewarded: Int,
    val taskCode: String
)