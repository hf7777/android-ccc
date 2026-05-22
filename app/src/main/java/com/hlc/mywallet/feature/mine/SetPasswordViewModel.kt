package com.hlc.mywallet.feature.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.SliderCaptchaResp
import com.hlc.mywallet.feature.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _sliderCaptchaState = MutableSharedFlow<ApiResult<SliderCaptchaResp>>()
    val sliderCaptchaState: SharedFlow<ApiResult<SliderCaptchaResp>> = _sliderCaptchaState.asSharedFlow()

    private val _sendPasswordOtpState = MutableSharedFlow<ApiResult<Unit>>()
    val sendPasswordOtpState: SharedFlow<ApiResult<Unit>> = _sendPasswordOtpState.asSharedFlow()

    private val _resetPasswordState = MutableSharedFlow<ApiResult<Unit>>()
    val resetPasswordState: SharedFlow<ApiResult<Unit>> = _resetPasswordState.asSharedFlow()

    private val _logoutState = MutableSharedFlow<ApiResult<Unit>>()
    val logoutState: SharedFlow<ApiResult<Unit>> = _logoutState.asSharedFlow()

    fun sliderCaptcha() {
        viewModelScope.launch {
            _sliderCaptchaState.emit(ApiResult.Loading)
            _sliderCaptchaState.emit(mainRepository.sliderCaptcha())
        }
    }

    fun sendPasswordOtp(phone: String, captchaId: String, sliderPosition: String) {
        viewModelScope.launch {
            _sendPasswordOtpState.emit(ApiResult.Loading)
            _sendPasswordOtpState.emit(userRepository.sendPasswordOtp(phone, captchaId, sliderPosition))
        }
    }

    fun resetPassword(phone: String, password: String, confirmPassword: String, otp: String) {
        viewModelScope.launch {
            _resetPasswordState.emit(ApiResult.Loading)
            _resetPasswordState.emit(userRepository.resetPassword(phone, password, confirmPassword, otp))
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.emit(ApiResult.Loading)
            _logoutState.emit(userRepository.logout())
        }
    }
}
