package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/20
 *
 * | 字段 | 类型 | 说明 |
 * | --- | --- | --- |
 * | taskCode | String | 任务编码 |
 * | taskName | String | 任务名称 |
 * | startTime | DateTime | 活动开始时间 |
 * | endTime | DateTime | 活动结束时间 |
 * | rewardAmount | BigDecimal | 每邀请一人的奖励金额 |
 * | subordinates | Array | 已邀请的下级列表 |
 *
 * ### subordinates 子字段
 *
 * | 字段 | 类型 | 说明 |
 * | --- | --- | --- |
 * | subordinateAntId | String | 下级用户 ID |
 * | subordinateUsername | String | 下级用户名（已脱敏） |
 * | phone | String | 下级手机号 |
 * | rewardAmount | BigDecimal | 该邀请可带来的奖励金额 |
 * | status | String | 状态：todo=下级未完成新手任务, done=可领取, claimed=已领取 |
 * | isRewarded | Integer | 是否领取奖励(0-未完成,1-已完成) |
 * | isCompleted | Integer | 是否完成活动 (0-未领取,1-已领取) |
 * | claimTime | DateTime | 领取时间 |
 */
data class ReferralResp(
    val endTime: String?,
    val rewardAmount: Double?,
    val startTime: String?,
    val subordinates: List<Subordinate>?,
    val taskCode: String?,
    val taskName: String?
)

data class Subordinate(
    val claimTime: Any?,
    val isCompleted: Int?,
    val isRewarded: Int?,
    val phone: String?,
    val rewardAmount: Double?,
    val status: String?,
    val subordinateAntId: String?,
    val subordinateUsername: String?
)
