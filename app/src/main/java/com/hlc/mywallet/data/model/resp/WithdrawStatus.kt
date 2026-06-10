package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Wade
 * @since 2026/6/2
 *
 * | `data` | object | 提现状态信息 |
 * | `data.autoPayoutStatus` | string | 自动代付开关状态：enable/disable |
 * | `data.autoBuyTime` | date | 自动代付状态变更时间（关闭时间） |
 * | `data.autoBuyEnableTime` | date | 自动代付可开启时间（前端可用于实时倒计时） |
 * | `data.autoBuyRemainingSeconds` | number | 自动代付可开启剩余秒数，0表示可立即开启 |
 * | `data.nextWithdrawTime` | date | 距离可提现时间（基于代收订单） |
 * | `data.remainingSeconds` | number | 距离可提现剩余秒数，0表示可立即提现 |
 * | `data.bankCard` | object | 银行卡信息，未绑定返回 null |
 * | `data.bankCard.id` | string | 银行卡ID |
 * | `data.bankCard.ifsc` | string | IFSC编码 |
 * | `data.bankCard.accountNo` | string | 银行卡号 |
 * | `data.bankCard.beneName` | string | 受益人名称 |
 * | `data.bankCard.accountName` | string | 账户名称 |
 * | `data.maxWithdrawTimes` | number | 当月最大提现次数（配置值） |
 * | `data.currentWithdrawTimes` | number | 当月已提现次数 |
 * | `data.minWithdrawAmount` | number | 最小提现金额 |
 * | `data.availableBalance` | number | 可提现金额（可用积分） |
 * | `data.description` | string | 文字描述（自动代付相关提示） |
 */
data class WithdrawStatus(
    val autoBuyRemainingSeconds: Long?,
    val autoPayoutStatus: String?,
    val availableBalance: String?,
    val bankCard: BankCard?,
    val currentWithdrawTimes: Int,
    val description: String?,
    val maxWithdrawTimes: Int,
    val minWithdrawAmount: String?,
    val nextWithdrawTime: Long?,
)

@Parcelize
data class BankCard(
    val id: String?,
    val ifsc: String?,
    val accountNo: String?,
    val beneName: String?,
    val accountName: String?,
) : Parcelable