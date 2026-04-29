package com.hlc.lib_base.router

import android.content.Context
import android.net.Uri
import android.os.Bundle

/**
 * 自定义协议处理器
 * 用于处理 action://、app:// 等自定义协议
 */
interface SchemeHandler {
    
    /**
     * 处理协议跳转
     * @param context 上下文
     * @param uri 协议 URI
     * @param extras 额外参数
     * @return true 处理成功，false 处理失败
     */
    fun handle(context: Context, uri: Uri, extras: Bundle): Boolean
}
