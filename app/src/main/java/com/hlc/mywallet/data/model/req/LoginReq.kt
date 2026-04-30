package com.hlc.mywallet.data.model.req

data class LoginRequest(
    val username: String,
    val password: String,
    val code: String,
    val uuid: String
)
