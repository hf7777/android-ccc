package com.hlc.mywallet.feature.mine.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.req.RegisterReq
import com.hlc.mywallet.data.model.resp.SliderCaptchaResp
import com.hlc.mywallet.feature.main.MainRepository
import com.hlc.mywallet.feature.mine.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _sliderCaptchaState = MutableSharedFlow<ApiResult<SliderCaptchaResp>>()
    val sliderCaptchaState = _sliderCaptchaState.asSharedFlow()

    private val _sendOtpState = MutableSharedFlow<ApiResult<Unit>>()
    val sendOtpState = _sendOtpState.asSharedFlow()

    private val _registerState = MutableSharedFlow<ApiResult<Unit>>()
    val registerState = _registerState.asSharedFlow()

    fun sliderCaptcha() {
        viewModelScope.launch {
            _sliderCaptchaState.emit(ApiResult.Loading)
            _sliderCaptchaState.emit(mainRepository.sliderCaptcha())
        }
    }

    fun sendOtp(phone: String, captchaId: String, sliderPosition: String) {
        viewModelScope.launch {
            _sendOtpState.emit(ApiResult.Loading)
            _sendOtpState.emit(userRepository.sendPasswordOtp(phone, captchaId, sliderPosition))
        }
    }

    fun register(registerReq: RegisterReq) {
        viewModelScope.launch {
            _registerState.emit(ApiResult.Loading)
            _registerState.emit(userRepository.register(registerReq))
        }
    }
}
