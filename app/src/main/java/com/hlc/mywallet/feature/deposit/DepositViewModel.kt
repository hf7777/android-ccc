package com.hlc.mywallet.feature.deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.DepositInrResp
import com.hlc.mywallet.data.model.resp.InrDetailResp
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import com.hlc.mywallet.data.model.resp.UsdtPayResp
import com.hlc.mywallet.feature.mine.UserRepository
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
    private val repository: DepositRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _depositInrResultFlow = MutableSharedFlow<ApiResult<DepositInrResp>>()
    val depositInrResultFlow: SharedFlow<ApiResult<DepositInrResp>> =
        _depositInrResultFlow.asSharedFlow()

    private val _priceInfoFlow = MutableStateFlow<PriceInfoResp?>(null)
    val priceInfoFlow: StateFlow<PriceInfoResp?> = _priceInfoFlow.asStateFlow()

    private val _myWalletResultFlow = MutableSharedFlow<ApiResult<List<MyWalletResp>>>()
    val myWalletResultFlow: SharedFlow<ApiResult<List<MyWalletResp>>> = _myWalletResultFlow.asSharedFlow()

    private val _usdtPayResultFlow = MutableSharedFlow<ApiResult<UsdtPayResp>>()
    val usdtPayResultFlow: SharedFlow<ApiResult<UsdtPayResp>> = _usdtPayResultFlow.asSharedFlow()

    private val _inrGrabFlow = MutableSharedFlow<ApiResult<String>>()
    val inrGrabFlow: SharedFlow<ApiResult<String>> = _inrGrabFlow.asSharedFlow()

    private val _inrDetailFlow = MutableSharedFlow<ApiResult<InrDetailResp>>()
    val inrDetailFlow: SharedFlow<ApiResult<InrDetailResp>> = _inrDetailFlow.asSharedFlow()

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

    /**
     * 获取钱包列表
     * 优先从缓存读取，缓存不存在则请求接口
     */
    fun getMyWallet() {
        viewModelScope.launch {
            _myWalletResultFlow.emit(ApiResult.Loading)
            
            // 先尝试从缓存获取
            val cachedWallets = userRepository.getCachedMyWallet()
            if (!cachedWallets.isNullOrEmpty()) {
                _myWalletResultFlow.emit(ApiResult.Success(cachedWallets))
            } else {
                // 缓存不存在，请求接口
                val result = userRepository.getMyWallet()
                _myWalletResultFlow.emit(result)
            }
        }
    }

    fun depositInrGrab(platformOrderNo: String, walletId: String    ) {
        viewModelScope.launch {
            _inrGrabFlow.emit(ApiResult.Loading)
            val result = repository.depositInrGrab(platformOrderNo, walletId)
            _inrGrabFlow.emit(result)
        }
    }

    fun getInrDetail(grabRecordId: String) {
        viewModelScope.launch {
            _inrDetailFlow.emit(ApiResult.Loading)
            val result = repository.inrDetail(grabRecordId)
            _inrDetailFlow.emit(result)
        }
    }
}
