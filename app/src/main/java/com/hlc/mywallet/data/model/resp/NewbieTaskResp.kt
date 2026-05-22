package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/19
 *
 * | 字段 | 类型 | 说明 |
 * | --- | --- | --- |
 * | taskCode | String | 任务编码（如 bind_telegram, set_pin, add_tool, complete_deposit） |
 * | taskName | String | 任务名称 |
 * | taskIcon | String | 任务图标 URL |
 * | rewardAmount | BigDecimal | 奖励金额（USDT） |
 * | tutorialId | Long | 关联教程 ID |
 * | status | String | 状态：todo=未完成, done=已完成待领取, claimed=已领取 |
 * | isRewarded | Integer | 是否已领取奖励（0=否, 1=是） |
 */
data class NewbieTaskResp(
    val isRewarded: Int?,
    val rewardAmount: String?,
    val status: String?,
    val taskCode: String?,
    val taskName: String?,
    val tutorialId: String?,
) {
    var actionRoute: String? = null
    var img: Int? = null
    var isDone: Boolean = status == "done" || status == "claimed"
}
