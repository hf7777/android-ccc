package com.hlc.mywallet.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.StringUtils
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.CsUrlResp
import com.hlc.mywallet.feature.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<HomeEvent>()
    val eventFlow: SharedFlow<HomeEvent> = _eventFlow.asSharedFlow()

    private val _customerServiceUrlFlow = MutableSharedFlow<ApiResult<CsUrlResp>>()
    val customerServiceUrlFlow: SharedFlow<ApiResult<CsUrlResp>> = _customerServiceUrlFlow.asSharedFlow()

    fun loadCustomerServiceUrl() {
        viewModelScope.launch {
            _customerServiceUrlFlow.emit(ApiResult.Loading)
            _customerServiceUrlFlow.emit(mainRepository.getCustomerServiceUrl())
        }
    }

    fun loadData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh
                )
            }

            supervisorScope {
                val bannersDeferred = async { repository.getBanners() }
                val priceDeferred = async { repository.getPriceInfo() }
                val tutorialsDeferred = async { repository.getTutorials() }

                val bannersResult = bannersDeferred.await()
                val priceResult = priceDeferred.await()
                val tutorialsResult = tutorialsDeferred.await()

                _uiState.update { currentState ->
                    currentState.copy(
                        banners = bannersResult.dataOr(currentState.banners),
                        priceInfo = priceResult.dataOr(currentState.priceInfo),
                        tutorials = tutorialsResult.dataOr(currentState.tutorials),
                        isLoading = false,
                        isRefreshing = false
                    )
                }

                emitErrorIfNeeded(bannersResult)
                emitErrorIfNeeded(priceResult)
                emitErrorIfNeeded(tutorialsResult)
            }
        }
    }

    private suspend fun emitErrorIfNeeded(result: ApiResult<*>) {
        if (result is ApiResult.Error) {
            _eventFlow.emit(HomeEvent.ShowError(result.exception.message ?: StringUtils.getString(R.string.request_failed)))
        }
    }

    private fun <T> ApiResult<T>.dataOr(defaultValue: T): T {
        return when (this) {
            is ApiResult.Success -> data
            else -> defaultValue
        }
    }
}
