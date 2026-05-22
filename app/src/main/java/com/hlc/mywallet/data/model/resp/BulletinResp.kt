package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/20
 *
 * | id | Long | 公告主键 ID |
 * | title | String | 公告标题 |
 * | content | String | 公告内容（HTML 格式） |
 * | imageUrl | String | 公告图片 URL（可为 null） |
 * | jumpRoute | String | 点击跳转的路由路径（可为 null） |
 * | autoConfirm | String | 是否允许手动确认：Y=允许用户手动点确认，N=仅系统自动确认 |
 * | sortOrder | Integer | 排序值（越小越靠前） |
 * | status | String | 状态：enable，1=close |
 * | remark | String | 备注 |
 * | createTime | DateTime | 创建时间 |
 *
 */
data class BulletinResp(
    val autoConfirm: String?,
    val content: String?,
    val createBy: Int?,
    val createTime: String?,
    val id: String?,
    val imageUrl: String?,
    val jumpRoute: String?,
    val sortOrder: Int?,
    val status: String?,
    val title: String?,
    val updateBy: Int?,
    val updateTime: String?
)