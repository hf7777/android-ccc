package com.hlc.mywallet.feature.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.SublineResp
import com.hlc.mywallet.data.model.resp.TeamStatisticsResp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: TeamRepository
) : ViewModel() {

    private val _teamStatisticsState = MutableStateFlow<ApiResult<TeamStatisticsResp>>(ApiResult.Idle)
    val teamStatisticsState: StateFlow<ApiResult<TeamStatisticsResp>> = _teamStatisticsState

    private val _sublineListState = MutableStateFlow<ApiResult<List<SublineResp>>>(ApiResult.Idle)
    val sublineListState: StateFlow<ApiResult<List<SublineResp>>> = _sublineListState

    fun getTeamStatistics() {
        viewModelScope.launch {
            _teamStatisticsState.value = ApiResult.Loading
            _teamStatisticsState.value = repository.getTeamStatistics()
        }
    }

    fun getSublineList(level: String) {
        viewModelScope.launch {
            _sublineListState.value = ApiResult.Loading
            _sublineListState.value = repository.sublineList(level)
        }
    }
}
