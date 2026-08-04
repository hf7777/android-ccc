package com.hlc.mywallet.fcm

/**
 * FCM 通知点击行为
 * 后续新增跳转类型时直接扩展该 sealed class 即可
 */
sealed class FcmNotificationAction {

    /**
     * 打开账单页并默认切换到 Withdrawal 标签
     */
    data object OpenBillsWithdrawal : FcmNotificationAction()

    /**
     * 未知/兜底行为，仅打开 App（MainActivity）
     */
    data object Default : FcmNotificationAction()

    companion object {
        private const val ACTION_BILLS_WITHDRAWAL = "bills_withdrawal"

        fun toActionString(action: FcmNotificationAction): String? {
            return when (action) {
                is OpenBillsWithdrawal -> ACTION_BILLS_WITHDRAWAL
                is Default -> null
            }
        }
    }
}
