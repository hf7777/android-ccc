package com.hlc.mywallet.feature.mine.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.req.BankInfoReq
import com.hlc.mywallet.data.model.resp.WithdrawStatus
import com.hlc.mywallet.feature.mine.UserRepository
import com.hlc.mywallet.feature.wallet.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val repository: UserRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _withdrawStatusFlow = MutableSharedFlow<ApiResult<WithdrawStatus>>()
    val withdrawStatusFlow: SharedFlow<ApiResult<WithdrawStatus>> = _withdrawStatusFlow.asSharedFlow()

    private val _submitWithdrawFlow = MutableSharedFlow<ApiResult<Unit>>()
    val submitWithdrawFlow: SharedFlow<ApiResult<Unit>> = _submitWithdrawFlow.asSharedFlow()

    private val _autoTradingFlow = MutableSharedFlow<ApiResult<Unit>>()
    val autoTradingFlow: SharedFlow<ApiResult<Unit>> = _autoTradingFlow.asSharedFlow()

    private val _bankCardFlow = MutableSharedFlow<ApiResult<Unit>>()
    val bankCardFlow: SharedFlow<ApiResult<Unit>> = _bankCardFlow.asSharedFlow()

    fun getWithdrawStatus() {
        viewModelScope.launch {
            _withdrawStatusFlow.emit(ApiResult.Loading)
            _withdrawStatusFlow.emit(repository.getWithdrawStatus())
        }
    }

    fun submitWithdraw(amount: String, pin: String, otp: String) {
        viewModelScope.launch {
            _submitWithdrawFlow.emit(ApiResult.Loading)
            _submitWithdrawFlow.emit(repository.submitWithdraw(amount, pin, otp))
        }
    }

    fun toggleAutoTrading(autoPayoutStatus: String?) {
        viewModelScope.launch {
            _autoTradingFlow.emit(ApiResult.Loading)
            val result = if (autoPayoutStatus.equals(AUTO_STATUS_ENABLE, ignoreCase = true)) {
                repository.stopAutoBuy()
            } else {
                repository.startAutoBuy()
            }
            _autoTradingFlow.emit(result)
        }
    }

    fun addBankCard(bankInfoReq: BankInfoReq) {
        viewModelScope.launch {
            _bankCardFlow.emit(ApiResult.Loading)
            _bankCardFlow.emit(walletRepository.addBankCard(bankInfoReq))
        }
    }

    fun editBankCard(bankInfoReq: BankInfoReq) {
        viewModelScope.launch {
            _bankCardFlow.emit(ApiResult.Loading)
            _bankCardFlow.emit(walletRepository.editBankCard(bankInfoReq))
        }
    }

    companion object {
        private const val AUTO_STATUS_ENABLE = "enable"
    }
}

