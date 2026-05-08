package com.hlc.lib_base.net

/**
 * 401 未授权处理器
 * 由 app 模块实现具体的跳转逻辑
 */
interface UnauthorizedHandler {
    fun handleUnauthorized()
}

/**
 * 全局 401 处理器持有者
 */
object UnauthorizedHandlerHolder {
    private var handler: UnauthorizedHandler? = null
    
    fun setHandler(handler: UnauthorizedHandler) {
        this.handler = handler
    }
    
    fun handle() {
        handler?.handleUnauthorized()
    }
}
