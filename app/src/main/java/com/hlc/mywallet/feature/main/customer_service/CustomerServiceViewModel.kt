package com.hlc.mywallet.feature.main.customer_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.CustomerServiceResp
import com.hlc.mywallet.feature.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerServiceViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _customerServiceState = MutableSharedFlow<ApiResult<List<CustomerServiceResp>>>()
    val customerServiceState = _customerServiceState.asSharedFlow()

    fun getCustomerService() {
        viewModelScope.launch {
            _customerServiceState.emit(ApiResult.Loading)
            _customerServiceState.emit(mainRepository.customerService())
        }
    }
}
