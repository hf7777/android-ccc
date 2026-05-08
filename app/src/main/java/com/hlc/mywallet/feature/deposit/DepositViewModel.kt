package com.hlc.mywallet.feature.deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.UsdtPayResp
import com.hlc.mywallet.data.model.resp.DepositInrResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DepositViewModel @Inject constructor(
    private val repository: DepositRepository
) : ViewModel() {

    private val _depositInrResultFlow = MutableSharedFlow<ApiResult<DepositInrResp>>()
    val depositInrResultFlow: SharedFlow<ApiResult<DepositInrResp>> =
        _depositInrResultFlow.asSharedFlow()

    private val _priceInfoFlow = MutableStateFlow<PriceInfoResp?>(null)
    val priceInfoFlow: StateFlow<PriceInfoResp?> = _priceInfoFlow.asStateFlow()

    private val _usdtPayResultFlow = MutableSharedFlow<ApiResult<UsdtPayResp>>()
    val usdtPayResultFlow: SharedFlow<ApiResult<UsdtPayResp>> = _usdtPayResultFlow.asSharedFlow()

    fun getDepositInrList(page: Int, pageSize: Int = 20) {
        viewModelScope.launch {
            _depositInrResultFlow.emit(repository.getDepositInrList(page, pageSize))
        }
    }

    fun loadCachedPriceInfo() {
        viewModelScope.launch {
            _priceInfoFlow.value = repository.getCachedPriceInfo()
        }
    }

    fun usdtPay(usdtAmount: Int) {
        viewModelScope.launch {
            _usdtPayResultFlow.emit(ApiResult.Loading)
            val result = repository.usdtPay(usdtAmount)
            _usdtPayResultFlow.emit(result)
        }
    }
}
