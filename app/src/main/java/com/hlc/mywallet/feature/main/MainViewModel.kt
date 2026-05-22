package com.hlc.mywallet.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.BalanceType
import com.hlc.mywallet.data.model.resp.BindCodeResp
import com.hlc.mywallet.data.model.resp.BulletinResp
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.feature.mine.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _myWalletResultFlow = MutableSharedFlow<ApiResult<List<MyWalletResp>>>()
    val myWalletResultFlow: SharedFlow<ApiResult<List<MyWalletResp>>> = _myWalletResultFlow.asSharedFlow()

    private val _bindCodeResultFlow = MutableStateFlow<ApiResult<BindCodeResp>>(ApiResult.Idle)
    val bindCodeResultFlow = _bindCodeResultFlow.asStateFlow()

    private val _sendPinOtpState = MutableSharedFlow<ApiResult<Unit>>()
    val sendPinOtpState = _sendPinOtpState.asSharedFlow()

    private val _setPinState = MutableSharedFlow<ApiResult<Unit>>()
    val setPinState = _setPinState.asSharedFlow()

    private val _checkPinState = MutableSharedFlow<ApiResult<Boolean>>()
    val checkPinState = _checkPinState.asSharedFlow()

    private val _sendTgOtpState = MutableSharedFlow<ApiResult<Unit>>()
    val sendTgOtpState = _sendTgOtpState.asSharedFlow()

    private val _balanceTypeState = MutableStateFlow<ApiResult<List<BalanceType>>>(ApiResult.Idle)
    val balanceTypeState = _balanceTypeState.asStateFlow()

    private val _bulletinListState = MutableStateFlow<ApiResult<List<BulletinResp>>>(ApiResult.Idle)
    val bulletinListState = _bulletinListState.asStateFlow()

    private val _confirmBulletinState = MutableSharedFlow<ApiResult<Unit>>()
    val confirmBulletinState = _confirmBulletinState.asSharedFlow()

    fun getMyWallet() {
        viewModelScope.launch {
            val result = userRepository.getMyWallet()
            _myWalletResultFlow.emit(result)
        }
    }

    suspend fun getCachedMyWallet(): List<MyWalletResp>? {
        return userRepository.getCachedMyWallet()
    }

    fun getBindCode() {
        viewModelScope.launch {
            val result = mainRepository.getBindCode()
            _bindCodeResultFlow.value = ApiResult.Loading
            _bindCodeResultFlow.value = result
        }
    }

    fun sendPinOtp() {
        viewModelScope.launch {
            _sendPinOtpState.emit(ApiResult.Loading)
            val result = mainRepository.sendPinOtp()
            _sendPinOtpState.emit(result)
        }
    }

    fun setPin(pin: String, confirmPin: String, otp: String) {
        viewModelScope.launch {
            _setPinState.emit(ApiResult.Loading)
            val result = mainRepository.setPin(pin, confirmPin, otp)
            _setPinState.emit(result)
        }
    }

    fun checkPin(pin: String) {
        viewModelScope.launch {
            _checkPinState.emit(ApiResult.Loading)
            _checkPinState.emit(mainRepository.checkPin(pin))
        }
    }

    fun sendTgOtp(phone: String,channelCode: String) {
        viewModelScope.launch {
            _sendTgOtpState.emit(ApiResult.Loading)
            _sendTgOtpState.emit(mainRepository.sendTgOtp(phone, channelCode))
        }
    }

    fun antBalanceType() {
        viewModelScope.launch {
            _balanceTypeState.emit(mainRepository.antBalanceType())
        }
    }

    fun getBulletinList() {
        viewModelScope.launch {
            _bulletinListState.value = ApiResult.Loading
            _bulletinListState.value = mainRepository.bulletinList()
        }
    }

    fun consumeBulletinListState() {
        _bulletinListState.value = ApiResult.Idle
    }

    fun confirmBulletin(announcementId: String) {
        if (announcementId.isBlank()) return
        viewModelScope.launch {
            _confirmBulletinState.emit(mainRepository.confirmBulletin(announcementId))
        }
    }
}
