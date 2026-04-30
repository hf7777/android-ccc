package com.hlc.mywallet.data.model.resp

data class LoginResp(
    val accessToken: String,
    val expireIn: Long,
    val clientId: String
)
