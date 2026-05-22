package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/13
 */
data class ApplyPermissionResp(
    val authStatus: String?,
    val channelCode: String?,
    val exists: Boolean?,
    val phone: String?,
    val token: String?,
    val tokenExpireDate: String?,
    val upiAccount: String?,
    val valid: Int?,
    val errorMsg: String?
)