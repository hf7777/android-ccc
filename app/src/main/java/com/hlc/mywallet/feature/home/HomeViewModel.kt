package com.hlc.mywallet.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.feature.home.model.DemoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.blankj.utilcode.util.LogUtils
import com.hlc.mywallet.data.model.resp.BannersResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {
    private val _bannerUiState = MutableStateFlow<ApiResult<List<BannersResp>>>(ApiResult.Idle)
    val bannerUiState: StateFlow<ApiResult<List<BannersResp>>> = _bannerUiState.asStateFlow()

    private val _priceUiState = MutableStateFlow<ApiResult<PriceInfoResp>>(ApiResult.Idle)
    val priceUiState: StateFlow<ApiResult<PriceInfoResp>> = _priceUiState.asStateFlow()

    fun getBanners() {
        viewModelScope.launch {
            val result = repository.getBanners()
            _bannerUiState.value = result
        }
    }

    fun getPriceInfo() {
        viewModelScope.launch {
            val result = repository.getPriceInfo()
            _priceUiState.value = result
        }
    }
}
