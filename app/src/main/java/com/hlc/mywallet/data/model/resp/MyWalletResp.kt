package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * @author Wade
 * @since 2026/5/9
 *
 * data[].id	string	钱包 ID
 * data[].phone	string	手机号
 * data[].upi	string	UPI ID
 * data[].channelCode	string	钱包渠道编码
 * data[].channelName	string	钱包渠道名称
 * data[].walletType	string	钱包类型
 * data[].status	string	状态
 * data[].onlineStatus	string	在线状态
 * data[].sellStatus	string	提现开关
 */
@Parcelize
data class MyWalletResp(
    val antId: String,
    val autoBuyStatus: @RawValue Any?,
    val beginCreateTime: @RawValue Any?,
    val beginSuccessTime: @RawValue Any?,
    val channelCode: String?,
    val channelName: String?,
    val createBy: String?,
    val createTime: String?,
    val id: String?,
    val onlineStatus: String?,
    val phone: String?,
    val sellStatus: String?,
    val status: String?,
    val updateBy: String?,
    val updateTime: String?,
    val upi: String?,
    val username: String?,
    val walletType: String?
) : Parcelable
