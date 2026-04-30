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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ApiResult<DemoItem>>(ApiResult.Loading)
    val uiState: StateFlow<ApiResult<DemoItem>> = _uiState.asStateFlow()

    fun loadDemo(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = ApiResult.Loading
            }
            val result = repository.loadDemo()
            _uiState.value = result
            when (result) {
                is ApiResult.Success -> LogUtils.d("Load demo success: ${result.data}")
                is ApiResult.Error -> LogUtils.e("Load demo failed", result.exception)
                is ApiResult.Loading -> {}
            }
        }
    }
}
