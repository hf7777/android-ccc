package com.hlc.mywallet.data.model.req

/**
 * @author Wade
 * @since 2026/5/20
 */
data class RegisterReq(
    val confirmPassword: String,
    val inviteCode: String,
    val otp: String,
    val password: String,
    val phone: String
)