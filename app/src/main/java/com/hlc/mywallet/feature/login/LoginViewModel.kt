package com.hlc.mywallet.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.feature.home.model.DemoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.blankj.utilcode.util.LogUtils
import com.hlc.mywallet.data.model.req.LoginReq
import com.hlc.mywallet.data.model.resp.CaptchaImage
import com.hlc.mywallet.data.model.resp.LoginResp
import com.hlc.mywallet.feature.home.HomeRepository
import com.hlc.mywallet.manager.UserManager
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val userManager: UserManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<ApiResult<CaptchaImage>>(ApiResult.Idle)
    val uiState: StateFlow<ApiResult<CaptchaImage>> = _uiState.asStateFlow()

    private val _loginUiState = MutableStateFlow<ApiResult<LoginResp>>(ApiResult.Idle)
    val LoginUiState: StateFlow<ApiResult<LoginResp>> = _loginUiState.asStateFlow()

    fun captchaImage(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = ApiResult.Loading
            }
            val result = repository.captchaImage()
            _uiState.value = result
        }
    }

    fun login(loginReq: LoginReq, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _loginUiState.value = ApiResult.Loading
            }
            val result = repository.login(loginReq)
            
            // 登录成功，保存 Token
            if (result is ApiResult.Success) {
                userManager.saveToken(result.data.accessToken)
                LogUtils.d("Login success, token saved: ${result.data.accessToken}")
            }
            
            _loginUiState.value = result
        }
    }
}
