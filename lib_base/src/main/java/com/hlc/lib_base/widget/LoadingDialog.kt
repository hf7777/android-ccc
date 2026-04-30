package com.hlc.lib_base.widget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hlc.lib_base.R

/**
 * Loading 对话框管理器
 * 统一管理 Activity 和 Fragment 的 Loading 对话框
 */
object LoadingDialog {
    
    private val dialogMap = mutableMapOf<String, Dialog>()
    
    /**
     * 显示 Loading 对话框
     * @param context Context
     * @param message 提示信息
     * @param cancelable 是否可取消
     */
    fun show(
        context: Context,
        message: String = context.getString(R.string.loading),
        cancelable: Boolean = false
    ) {
        val key = context.hashCode().toString()
        
        // 如果已经在显示，直接返回
        if (dialogMap[key]?.isShowing == true) return
        
        // 检查 Activity 状态
        if (context is FragmentActivity) {
            if (context.isFinishing || context.isDestroyed) return
        }
        
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null, false)
        view.findViewById<TextView>(R.id.tv_loading_message).text = message
        
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(cancelable)
            .create()
            .apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                show()
            }
        
        dialogMap[key] = dialog
    }
    
    /**
     * 隐藏 Loading 对话框
     */
    fun hide(context: Context) {
        val key = context.hashCode().toString()
        dialogMap[key]?.dismiss()
        dialogMap.remove(key)
    }
    
    /**
     * Fragment 显示 Loading
     */
    fun show(
        fragment: Fragment,
        message: String = fragment.getString(R.string.loading),
        cancelable: Boolean = false
    ) {
        if (!fragment.isAdded || fragment.isDetached) return
        show(fragment.requireContext(), message, cancelable)
    }
    
    /**
     * Fragment 隐藏 Loading
     */
    fun hide(fragment: Fragment) {
        if (!fragment.isAdded || fragment.isDetached) return
        hide(fragment.requireContext())
    }
    
    /**
     * 清除所有 Loading 对话框
     */
    fun clearAll() {
        dialogMap.values.forEach { it.dismiss() }
        dialogMap.clear()
    }
}

/**
 * Context 扩展函数
 */
fun Context.showLoading(message: String = getString(R.string.loading), cancelable: Boolean = false) {
    LoadingDialog.show(this, message, cancelable)
}

fun Context.hideLoading() {
    LoadingDialog.hide(this)
}

/**
 * Fragment 扩展函数
 */
fun Fragment.showLoading(message: String = getString(R.string.loading), cancelable: Boolean = false) {
    LoadingDialog.show(this, message, cancelable)
}

fun Fragment.hideLoading() {
    LoadingDialog.hide(this)
}
