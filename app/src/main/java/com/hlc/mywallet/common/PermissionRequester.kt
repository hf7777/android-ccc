package com.hlc.mywallet.common

import android.app.Activity
import android.content.Context
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * 统一收口项目里的权限申请逻辑。
 * 特殊权限由 XXPermissions 负责兼容不同厂商的跳转页面，业务层只关心最终是否授权。
 */
object PermissionRequester {

    fun hasOverlayPermission(context: Context): Boolean {
        return XXPermissions.isGranted(context, Permission.SYSTEM_ALERT_WINDOW)
    }

    fun requestOverlayPermission(
        activity: Activity,
        onResult: (granted: Boolean) -> Unit
    ) {
        XXPermissions.with(activity)
            .permission(Permission.SYSTEM_ALERT_WINDOW)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String>, allGranted: Boolean) {
                    onResult(allGranted)
                }

                override fun onDenied(permissions: List<String>, doNotAskAgain: Boolean) {
                    onResult(false)
                }
            })
    }
}
