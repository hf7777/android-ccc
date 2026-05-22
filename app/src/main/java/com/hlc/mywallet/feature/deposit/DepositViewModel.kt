package com.hlc.mywallet.feature.deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.DepositInrResp
import com.hlc.mywallet.data.model.resp.InrDetailResp
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import com.hlc.mywallet.data.model.resp.UsdtPayResp
import com.hlc.mywallet.feature.main.MainRepository
import com.hlc.mywallet.feature.mine.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class DepositViewModel @Inject constructor(
    private val repository: DepositRepository,
    private val mainRepository: MainRepository,
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

    private val _cancelInrOrderFlow = MutableSharedFlow<ApiResult<Unit>>()
    val cancelInrOrderFlow: SharedFlow<ApiResult<Unit>> = _cancelInrOrderFlow.asSharedFlow()

    private val _uploadImageFlow = MutableSharedFlow<ApiResult<String>>()
    val uploadImageFlow: SharedFlow<ApiResult<String>> = _uploadImageFlow.asSharedFlow()

    private val _inrDepositConfirmFlow = MutableSharedFlow<ApiResult<Unit>>()
    val inrDepositConfirmFlow: SharedFlow<ApiResult<Unit>> = _inrDepositConfirmFlow.asSharedFlow()

    fun getDepositInrList(
        page: Int,
        pageSize: Int = 20,
        minAmount: String? = null,
        maxAmount: String? = null,
        orderAmountSort: String? = null
    ) {
        viewModelScope.launch {
            _depositInrResultFlow.emit(
                repository.getDepositInrList(
                    page = page,
                    pageSize = pageSize,
                    minAmount = minAmount,
                    maxAmount = maxAmount,
                    orderAmountSort = orderAmountSort
                )
            )
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

    fun getMyWallet() {
        viewModelScope.launch {
            _myWalletResultFlow.emit(ApiResult.Loading)
            val result = userRepository.getMyWallet()
            _myWalletResultFlow.emit(result)
        }
    }

    fun depositInrGrab(platformOrderNo: String, walletId: String) {
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

    fun cancelInrOrder(grabRecordId: String) {
        viewModelScope.launch {
            _cancelInrOrderFlow.emit(ApiResult.Loading)
            val result = repository.cancelInrOrder(grabRecordId)
            _cancelInrOrderFlow.emit(result)
        }
    }

    fun uploadImage(file: MultipartBody.Part) {
        viewModelScope.launch {
            _uploadImageFlow.emit(ApiResult.Loading)
            val result = mainRepository.uploadImage(file)
            _uploadImageFlow.emit(result)
        }
    }

    fun inrDepositConfirm(platformOrderNo: String, grabId: String, utr: String, voucherUrl: String) {
        viewModelScope.launch {
            _inrDepositConfirmFlow.emit(ApiResult.Loading)
            val result = repository.inrDepositConfirm(platformOrderNo, grabId, utr, voucherUrl)
            _inrDepositConfirmFlow.emit(result)
        }
    }


}
