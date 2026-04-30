package com.hlc.mywallet.data.model.req

data class LoginReq(
    val tenantId: String = "1",
    val username: String,
    val password: String,
    val code: String,
    val uuid: String,
    val grantType: String = "password"
)
