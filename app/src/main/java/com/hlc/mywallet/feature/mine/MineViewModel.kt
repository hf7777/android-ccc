package com.hlc.mywallet.feature.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.UserStatisticsResp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _userStatisticsState = MutableStateFlow<ApiResult<UserStatisticsResp>>(ApiResult.Idle)
    val userStatisticsState: StateFlow<ApiResult<UserStatisticsResp>> = _userStatisticsState

    fun getUserStatistics() {
        viewModelScope.launch {
            _userStatisticsState.value = ApiResult.Loading
            _userStatisticsState.value = repository.getUserStatistics()
        }
    }

    fun getOrderInrList(yearMonth: String? = null) {
        viewModelScope.launch {

        }
    }
}
