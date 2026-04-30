package com.hlc.lib_base.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hjq.toast.Toaster
import com.hlc.lib_base.net.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * 收集 ApiResult Flow，自动处理 Error 状态（弹 Toast）
 * 
 * @param showLoading 是否在 Loading 时显示加载框
 * @param onLoading Loading 状态回调
 * @param onSuccess Success 状态回调
 * @param onError Error 状态回调（可选，默认弹 Toast）
 * 
 * 使用示例：
 * ```
 * viewModel.uiState.collectWithError(
 *     lifecycleOwner = this,
 *     onLoading = { showLoading() },
 *     onSuccess = { data ->
 *         binding.tvResult.text = data.toString()
 *     }
 * )
 * ```
 */
fun <T> Flow<ApiResult<T>>.collectWithError(
    lifecycleOwner: LifecycleOwner,
    showLoading: Boolean = false,
    onLoading: (() -> Unit)? = null,
    onSuccess: (T) -> Unit,
    onError: ((String) -> Unit)? = null
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        onLoading?.invoke()
                    }
                    is ApiResult.Success -> {
                        onSuccess(result.data)
                    }
                    is ApiResult.Error -> {
                        val errorMsg = result.exception.message ?: "请求失败"
                        if (onError != null) {
                            onError(errorMsg)
                        } else {
                            // 默认弹 Toast
                            Toaster.show(errorMsg)
                        }
                    }
                    else -> {} // Idle
                }
            }
        }
    }
}

/**
 * 收集多个 ApiResult Flow（简化版）
 * 
 * 使用示例：
 * ```
 * collectApiResults(
 *     viewModel.captchaState to { data: CaptchaImage ->
 *         binding.ivCode.loadBase64(data.img)
 *     },
 *     viewModel.loginState to { data: LoginResponse ->
 *         navigation(Routes.HOME)
 *     }
 * )
 * ```
 */
fun <T1, T2> LifecycleOwner.collectApiResults(
    vararg flows: Pair<Flow<ApiResult<*>>, (Any) -> Unit>
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flows.forEach { (flow, onSuccess) ->
                launch {
                    flow.collect { result ->
                        when (result) {
                            is ApiResult.Success -> {
                                @Suppress("UNCHECKED_CAST")
                                onSuccess(result.data as Any)
                            }
                            is ApiResult.Error -> {
                                Toaster.show(result.exception.message ?: "请求失败")
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
