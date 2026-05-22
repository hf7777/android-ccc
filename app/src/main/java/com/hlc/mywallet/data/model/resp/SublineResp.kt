package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/15
 *
 * | `data[].id` | string | 用户 ID |
 * | `data.avatar` | string | 头像地址 |
 * | `data[].username` | string | 用户名（脱敏） |
 * | `data[].createTime` | string | 注册时间 |
 * | `data[].commission` | string | 贡献佣金 |
 */
data class SublineResp(
    val avatar: String,
    val commission: String,
    val createTime: String,
    val id: String,
    val username: String
)