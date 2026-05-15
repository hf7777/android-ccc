package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Wade
 * @since 2026/5/12
 *
 * | `data[].channelCode` | string | 钱包渠道编码 |
 * | `data[].channelName` | string | 钱包渠道名称 |
 * | `data[].buyStatus` | string | 买状态：enable=开启, close=关闭 |
 * | `data[].sellStatus` | string | 卖状态：enable=开启, close=关闭 |
 */
@Parcelize
data class PayChannelResp(
    val autoBuyStatus: String,
    val buyStatus: String,
    val channelCode: String,
    val channelName: String,
    val createBy: Int,
    val createTime: String,
    val id: String,
    val sellStatus: String,
    val status: String,
    val thirdWalletType: String,
    val updateBy: Int,
    val updateTime: String
) : Parcelable
