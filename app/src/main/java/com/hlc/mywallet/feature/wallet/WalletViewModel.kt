package com.hlc.mywallet.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.ApplyPermissionResp
import com.hlc.mywallet.data.model.resp.CheckBindingResp
import com.hlc.mywallet.data.model.resp.PayChannelResp
import com.hlc.mywallet.data.model.resp.WalletListResp
import com.hlc.mywallet.feature.mine.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    data class WalletSellAction(
        val phone: String,
        val channelCode: String
    )

    private val _walletListState = MutableSharedFlow<ApiResult<WalletListResp>>()
    val walletListState = _walletListState.asSharedFlow()
    private val _walletSellState = MutableSharedFlow<ApiResult<WalletSellAction>>()
    val walletSellState = _walletSellState.asSharedFlow()
    private val _payChannelListState = MutableSharedFlow<ApiResult<List<PayChannelResp>>>()
    val payChannelListState = _payChannelListState.asSharedFlow()
    private val _upiListFlow = MutableSharedFlow<ApiResult<List<String>>>()
    val upiListFlow: SharedFlow<ApiResult<List<String>>> = _upiListFlow.asSharedFlow()

    private val _checkBindingFlow = MutableSharedFlow<ApiResult<CheckBindingResp>>()
    val checkBindingFlow: SharedFlow<ApiResult<CheckBindingResp>> = _checkBindingFlow.asSharedFlow()
    private val _applyPermissionFlow = MutableSharedFlow<ApiResult<ApplyPermissionResp>>()
    val applyPermissionFlow: SharedFlow<ApiResult<ApplyPermissionResp>> = _applyPermissionFlow.asSharedFlow()
    private val _loginWalletFlow = MutableSharedFlow<ApiResult<Unit>>()
    val loginWalletFlow: SharedFlow<ApiResult<Unit>> = _loginWalletFlow.asSharedFlow()
    private val _addWalletFlow = MutableSharedFlow<ApiResult<Unit>>()
    val addWalletFlow: SharedFlow<ApiResult<Unit>> = _addWalletFlow.asSharedFlow()
    private val _editUpiFlow = MutableSharedFlow<ApiResult<Unit>>()
    val editUpiFlow: SharedFlow<ApiResult<Unit>> = _editUpiFlow.asSharedFlow()
    private val _deAuthorizeFlow = MutableSharedFlow<ApiResult<Unit>>()
    val deAuthorizeFlow: SharedFlow<ApiResult<Unit>> = _deAuthorizeFlow.asSharedFlow()
    private val _relinkFlow = MutableSharedFlow<ApiResult<Boolean>>()
    val relinkFlow: SharedFlow<ApiResult<Boolean>> = _relinkFlow.asSharedFlow()



    fun getWalletList(page: Int, pageSize: Int = 20) {
        viewModelScope.launch {
            val params = buildJsonBody(
                "pageNum" to page.toString(),
                "pageSize" to pageSize.toString(),
                "timezone" to "IN"
            )
            _walletListState.emit(ApiResult.Loading)
            _walletListState.emit(repository.getWalletList(params))
        }
    }

    fun startSelling(phone: String, channelCode: String) {
        viewModelScope.launch {
            _walletSellState.emit(ApiResult.Loading)
            val result = repository.startSelling(phone, channelCode)
            when (result) {
                is ApiResult.Success -> {
                    _walletSellState.emit(
                        ApiResult.Success(
                            WalletSellAction(phone = phone, channelCode = channelCode)
                        )
                    )
                }

                is ApiResult.Error -> _walletSellState.emit(result)
                else -> Unit
            }
        }
    }

    fun closeSelling(phone: String, channelCode: String) {
        viewModelScope.launch {
            _walletSellState.emit(ApiResult.Loading)
            val result = repository.closeSelling(phone, channelCode)
            when (result) {
                is ApiResult.Success -> {
                    _walletSellState.emit(
                        ApiResult.Success(
                            WalletSellAction(phone = phone, channelCode = channelCode)
                        )
                    )
                }

                is ApiResult.Error -> _walletSellState.emit(result)
                else -> Unit
            }
        }
    }

    fun payChannelList(isBuy: Boolean) {
        viewModelScope.launch {
            _payChannelListState.emit(ApiResult.Loading)
            _payChannelListState.emit(
                if (isBuy) {
                    repository.payChannelList(buyStatus = STATUS_ENABLE)
                } else {
                    repository.payChannelList(sellStatus = STATUS_ENABLE)
                }
            )
        }
    }

    fun getUpiList(phone: String, channelCode: String) {
        viewModelScope.launch {
            _upiListFlow.emit(ApiResult.Loading)
            _upiListFlow.emit(repository.getUpiList(phone, channelCode))
        }
    }

    fun addWallet(phone: String, channelCode: String, otp: String, pin: String, upiId: String) {
        viewModelScope.launch {
            _addWalletFlow.emit(ApiResult.Loading)
            _addWalletFlow.emit(repository.addWallet(phone, channelCode, otp, pin, upiId))
        }
    }

    fun editUpi(phone: String, channelCode: String, newUpi: String) {
        viewModelScope.launch {
            _editUpiFlow.emit(ApiResult.Loading)
            _editUpiFlow.emit(repository.editUpi(phone, channelCode, newUpi))
        }
    }

    fun deAuthorize(phone: String, channelCode: String) {
        viewModelScope.launch {
            _deAuthorizeFlow.emit(ApiResult.Loading)
            _deAuthorizeFlow.emit(repository.deAuthorize(phone, channelCode))
        }
    }

    fun relink(phone: String, channelCode: String) {
        viewModelScope.launch {
            _relinkFlow.emit(ApiResult.Loading)
            _relinkFlow.emit(repository.relink(phone, channelCode))
        }
    }

    fun checkBinding() {
        viewModelScope.launch {
            _checkBindingFlow.emit(ApiResult.Loading)
            _checkBindingFlow.emit(userRepository.checkBinding())
        }
    }

    fun applyPermissionAndLogin(phone: String, channelCode: String, otp: String) {
        viewModelScope.launch {
            _applyPermissionFlow.emit(ApiResult.Loading)
            // 轮询期间只发送一次事件即可
            var hasEmittedApplySuccess = false
            while (true) {
                when (val applyResult = repository.applyPermission(phone, channelCode)) {
                    is ApiResult.Success -> {
                        if (!hasEmittedApplySuccess) {
                            _applyPermissionFlow.emit(applyResult)
                            hasEmittedApplySuccess = true
                        }
                        if (applyResult.data.authStatus == "3") {
                            when (val loginResult = repository.loginWallet(phone, channelCode, otp)) {
                                is ApiResult.Success -> _loginWalletFlow.emit(loginResult)
                                is ApiResult.Error -> _loginWalletFlow.emit(loginResult)
                                else -> Unit
                            }
                            break
                        }
                        delay(APPLY_PERMISSION_POLL_INTERVAL_MILLIS)
                    }

                    is ApiResult.Error -> {
                        _applyPermissionFlow.emit(applyResult)
                        break
                    }

                    else -> Unit
                }
            }
        }
    }


    companion object {
        private const val STATUS_ENABLE = "enable"
        private const val APPLY_PERMISSION_POLL_INTERVAL_MILLIS = 1_000L
    }
}
