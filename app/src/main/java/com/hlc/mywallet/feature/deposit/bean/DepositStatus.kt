package com.hlc.mywallet.feature.deposit.bean

/**
 * @author Wade
 * @since 2026/5/11
 */
enum class DepositStatus(val state: String, val desc: String) {
    GRAB("grab", "已抢单"),
    SUCCESS("success", "成功"),
    LOCKED("locked", "锁定"),
    CANCEL("cancel", "失效"),
    FAILED("failed", "失败")
}