package com.hlc.mywallet.feature.wallet.bean

import android.os.Parcelable
import com.hlc.mywallet.data.model.resp.PayChannelResp
import com.hlc.mywallet.data.model.resp.Wallet
import kotlinx.parcelize.Parcelize

/**
 * Pay Channel Setup 流程专用参数。
 *
 * 这个对象的职责是统一承接不同入口传入的流程基础信息，避免
 * PayChannelSetup1/2/3Fragment 直接依赖 PayChannelResp 或 Wallet。
 *
 * 目前有两种入口：
 * 1. 从 PayChannelFragment 进入，只能拿到渠道信息
 * 2. 从 WalletFragment 的 relink 进入，能拿到渠道信息和手机号
 *
 * 通过统一收敛为这个参数对象，后续步骤页只关心：
 * - channelName: 用于标题和文案展示
 * - channelCode: 用于后续接口请求
 * - phone: 如果入口已知手机号，可以直接回填并按需锁定输入框
 * - pin / otp: 流程内逐步补充的中间参数，便于三个步骤统一只传一个对象
 */
@Parcelize
data class PayChannelSetupArgs(
    val channelName: String,
    val channelCode: String,
    val phone: String = "",
    val pin: String = "",
    val otp: String = "",
    val description: String = "",
    val isAutoBuy: Boolean = false
) : Parcelable {

    companion object {

        fun fromPayChannel(channel: PayChannelResp, isAutoBuy: Boolean): PayChannelSetupArgs {
            return PayChannelSetupArgs(
                channelName = channel.channelName.orEmpty(),
                channelCode = channel.channelCode.orEmpty(),
                phone = "",
                description = channel.description.orEmpty(),
                isAutoBuy = isAutoBuy
            )
        }

        fun fromWallet(wallet: Wallet, isAutoBuy: Boolean): PayChannelSetupArgs {
            return PayChannelSetupArgs(
                channelName = wallet.channelName.orEmpty(),
                channelCode = wallet.channelCode.orEmpty(),
                phone = wallet.phone.orEmpty(),
                description = wallet.description.orEmpty(),
                isAutoBuy = isAutoBuy
            )
        }
    }
}
