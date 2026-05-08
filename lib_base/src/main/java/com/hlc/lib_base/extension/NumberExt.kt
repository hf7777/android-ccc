package com.hlc.lib_base.extension

import java.math.BigDecimal

/**
 * 去掉小数点后多余的 0
 * 例如：
 * "10.00" -> "10"
 * "10.50" -> "10.5"
 * "10.123" -> "10.123"
 * "10" -> "10"
 */
fun String?.stripTrailingZeros(): String {
    if (this.isNullOrEmpty()) return "0"
    
    return try {
        BigDecimal(this).stripTrailingZeros().toPlainString()
    } catch (e: Exception) {
        this
    }
}

/**
 * 格式化数字字符串，去掉多余的 0
 * 支持空值和非数字字符串
 */
fun String?.formatNumber(): String {
    return this.stripTrailingZeros()
}
