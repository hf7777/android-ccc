package com.hlc.mywallet.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.mywallet.R
import com.hlc.lib_base.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadDemo() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            when (val result = repository.loadDemo()) {
                is ApiResult.Success -> {
                    Timber.d("Load demo success: ${result.data}")
                    _uiState.value = HomeUiState.Success(result.data)
                }
                is ApiResult.Error -> {
                    Timber.e(result.exception, "Load demo failed")
                    _uiState.value = HomeUiState.Error(
                        result.exception.message ?: context.getString(R.string.request_failed)
                    )
                }
                is ApiResult.Loading -> {
                    _uiState.value = HomeUiState.Loading
                }
            }
        }
    }
}
