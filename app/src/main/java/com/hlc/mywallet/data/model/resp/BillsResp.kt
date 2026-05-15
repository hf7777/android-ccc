package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/14
 *
 * | `data.rows[].type` | string | 账单类型编码 |
 * | `data.rows[].typeText` | string | 账单类型显示文本 |
 * | `data.rows[].amount` | string | 金额 |
 * | `data.rows[].beforeBalance` | string | 变动前余额 |
 * | `data.rows[].afterBalance` | string | 变动后余额 |
 * | `data.rows[].utr` | string | UTR 交易参考号 |
 * | `data.rows[].direction` | string | 资金方向 |
 * | `data.rows[].createTime` | string | 创建时间 |
 */
data class BillsResp(
    val rows: List<Bill>,
    val total: Int
)

data class Bill(
    val afterBalance: String?,
    val amount: String?,
    val beforeBalance: String?,
    val createTime: String?,
    val direction: String?,
    val type: String?,
    val typeText: String?,
    val utr: String?
)
