package com.hlc.mywallet.data.model.resp

data class PriceInfoResp(
    val inrFeeRate: String?,
    val inrFeeSingle: String?,
    val marketUsdtRate: String?,
    val platUsdtRate: String?,
    val totalBalance: String?
)