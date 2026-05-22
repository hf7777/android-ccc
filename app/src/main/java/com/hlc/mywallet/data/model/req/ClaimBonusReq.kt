package com.hlc.mywallet.data.model.req

/**
 * @author Wade
 * @since 2026/5/19
 */
data class ClaimBonusReq(
    val taskCode: String,
    val targetId: String? = null
)
