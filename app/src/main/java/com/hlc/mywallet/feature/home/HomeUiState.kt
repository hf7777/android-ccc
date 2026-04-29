package com.hlc.mywallet.feature.home

import com.hlc.mywallet.feature.home.model.DemoItem

sealed interface HomeUiState {
    object Idle : HomeUiState
    object Loading : HomeUiState
    data class Success(val data: DemoItem) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
