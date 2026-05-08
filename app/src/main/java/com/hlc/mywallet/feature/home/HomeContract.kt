package com.hlc.mywallet.feature.home

import com.hlc.mywallet.data.model.resp.BannersResp
import com.hlc.mywallet.data.model.resp.PriceInfoResp
import com.hlc.mywallet.data.model.resp.TutorialResp

data class HomeUiState(
    val banners: List<BannersResp> = emptyList(),
    val priceInfo: PriceInfoResp? = null,
    val tutorials: List<TutorialResp> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)

fun HomeUiState.hasContent(): Boolean {
    return banners.isNotEmpty() || priceInfo != null || tutorials.isNotEmpty()
}

sealed interface HomeEvent {
    data class ShowError(val message: String) : HomeEvent
}
