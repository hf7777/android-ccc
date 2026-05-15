package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * @author Wade
 * @since 2026/5/12
 *
 *| 参数 | 类型 | 描述 |
 * | --- | --- | --- |
 * | `code` | number | 响应状态码，200 表示成功 |
 * | `msg` | string | 响应消息 |
 * | `data` | object | 分页数据 |
 * | `data.total` | number | 总记录数 |
 * | `data.rows` | array | 钱包列表 |
 * | `data.rows[].id` | string | 钱包 ID |
 * | `data.rows[].antId` | string | 用户 ID |
 * | `data.rows[].phone` | string | 手机号 |
 * | `data.rows[].upi` | string | UPI ID |
 * | `data.rows[].channelCode` | string | 钱包渠道编码 |
 * | `data.rows[].channelName` | string | 钱包渠道名称 |
 * | `data.rows[].walletType` | string | 钱包类型（upi） |
 * | `data.rows[].status` | string | 状态（enable / disable） |
 * | `data.rows[].onlineStatus` | string | 在线状态（Y / N） |
 * | `data.rows[].sellStatus` | string | 提现开关（open / close） |
 * | `data.rows[].minLimit` | number | 单笔最低限额 |
 * | `data.rows[].maxLimit` | number | 单笔最高限额 |
 * | `data.rows[].dailyLimit` | number | 每日累计限额 |
 */
data class WalletListResp(
    val rows: List<Wallet>,
    val total: Int
)

@Parcelize
data class Wallet(
    val antId: String?,
    val autoBuyStatus: String?,
    val channelAutoBuyStatus: String?,
    val channelCode: String?,
    val channelName: String?,
    val channelSellStatus: String?,
    val createTime: String?,
    val dailyLimit: String?,
    val id: String?,
    val maxLimit: String?,
    val minLimit: String?,
    val onlineStatus: String?,
    val phone: String?,
    val sellStatus: String?,
    val status: String?,
    val timezone: @RawValue Any?,
    val updateBy: Long?,
    val updateTime: String?,
    val upi: String?,
    val username: String?,
    val walletType: String?
) : Parcelable
