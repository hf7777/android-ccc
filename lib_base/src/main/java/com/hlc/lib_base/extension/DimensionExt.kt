package com.hlc.lib_base.extension

import com.blankj.utilcode.util.Utils
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * 尺寸单位转换扩展函数
 * 使用 AndroidAutoSize 进行适配
 * 自动使用全局 Application Context
 */

/**
 * dp 转 px
 * 使用示例：16.dp
 */
val Int.dp: Int
    get() = AutoSizeUtils.dp2px(Utils.getApp(), this.toFloat())

/**
 * sp 转 px
 * 使用示例：14.sp
 */
val Int.sp: Int
    get() = AutoSizeUtils.sp2px(Utils.getApp(), this.toFloat())

/**
 * Float dp 转 px
 * 使用示例：16.5f.dp
 */
val Float.dp: Int
    get() = AutoSizeUtils.dp2px(Utils.getApp(), this)

/**
 * Float sp 转 px
 * 使用示例：14.5f.sp
 */
val Float.sp: Int
    get() = AutoSizeUtils.sp2px(Utils.getApp(), this)

/**
 * px 转 dp
 * 使用示例：100.px2dp
 */
val Int.px2dp: Int
    get() = AutoSizeUtils.px2dp(Utils.getApp(), this.toFloat())

/**
 * px 转 sp
 * 使用示例：100.px2sp
 */
val Int.px2sp: Int
    get() = AutoSizeUtils.px2sp(Utils.getApp(), this.toFloat())
