package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/20
 *
 * | `data[].name` | string | 客服名称 |
 * | `data[].telegramUsername` | string | Telegram 用户名 |
 * | `data[].telegramLink` | string | Telegram 链接 |
 * | `data[].title` | string | 客服标题 |
 */
data class CustomerServiceResp(
    val name: String?,
    val telegramLink: String?,
    val telegramUsername: String?,
    val title: String?
)