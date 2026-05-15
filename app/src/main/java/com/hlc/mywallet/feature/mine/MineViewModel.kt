package com.hlc.mywallet.feature.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.BillsResp
import com.hlc.mywallet.data.model.resp.CheckBindingResp
import com.hlc.mywallet.data.model.resp.OrderInrResp
import com.hlc.mywallet.data.model.resp.OrderUsdtResp
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _userStatisticsState = MutableStateFlow<ApiResult<UserStatisticsResp>>(ApiResult.Idle)
    val userStatisticsState: StateFlow<ApiResult<UserStatisticsResp>> = _userStatisticsState

    private val _orderInrListFlow = MutableSharedFlow<ApiResult<OrderInrResp>>()
    val orderInrListFlow: SharedFlow<ApiResult<OrderInrResp>> = _orderInrListFlow.asSharedFlow()

    private val _orderUsdtListFlow = MutableSharedFlow<ApiResult<OrderUsdtResp>>()
    val orderUsdtListFlow: SharedFlow<ApiResult<OrderUsdtResp>> = _orderUsdtListFlow.asSharedFlow()
    private val _billsFlow = MutableSharedFlow<ApiResult<BillsResp>>()
    val billsFlow: SharedFlow<ApiResult<BillsResp>> = _billsFlow.asSharedFlow()

    fun getUserStatistics() {
        viewModelScope.launch {
            _userStatisticsState.value = ApiResult.Loading
            _userStatisticsState.value = repository.getUserStatistics()
        }
    }

    fun getOrderInrList(page: Int, pageSize: Int, yearMonth: String? = null) {
        viewModelScope.launch {
            _orderInrListFlow.emit(repository.getOrderInrList(page, pageSize, yearMonth))
        }
    }

    fun getUsdtList(page: Int, pageSize: Int, yearMonth: String? = null) {
        viewModelScope.launch {
            _orderUsdtListFlow.emit(repository.getUsdtList(page, pageSize, yearMonth))
        }
    }

    fun geBills(yearMonth: String? = null, type: String? = null) {
        viewModelScope.launch {
            _billsFlow.emit(repository.geBills(yearMonth, type))
        }
    }

}
