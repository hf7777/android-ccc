package com.hlc.mywallet.feature.bonus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.NewbieSummaryResp
import com.hlc.mywallet.data.model.resp.NewbieTaskResp
import com.hlc.mywallet.data.model.resp.ReferralResp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Wade
 * @since 2026/5/19
 */
@HiltViewModel
class BonusViewModel @Inject constructor(
    private val repository: BonusRepository
) : ViewModel() {

    private val _newbieSummaryState = MutableStateFlow<ApiResult<NewbieSummaryResp>>(ApiResult.Idle)
    val newbieSummaryState = _newbieSummaryState.asStateFlow()

    private val _newbieListState = MutableStateFlow<ApiResult<List<NewbieTaskResp>>>(ApiResult.Idle)
    val newbieListState = _newbieListState.asStateFlow()

    private val _referralListState = MutableStateFlow<ApiResult<ReferralResp>>(ApiResult.Idle)
    val referralListState = _referralListState.asStateFlow()

    private val _claimBonusFlow = MutableSharedFlow<ApiResult<Unit>>()
    val claimBonusFlow = _claimBonusFlow.asSharedFlow()

    private val _claimReferralBonusFlow = MutableSharedFlow<ApiResult<Unit>>()
    val claimReferralBonusFlow = _claimReferralBonusFlow.asSharedFlow()

    fun getNewbieSummary() {
        viewModelScope.launch {
            _newbieSummaryState.value = ApiResult.Loading
            _newbieSummaryState.value = repository.getNewbieSummary()
        }
    }

    fun getNewbieList() {
        viewModelScope.launch {
            _newbieListState.value = ApiResult.Loading
            _newbieListState.value = repository.newbieList()
        }
    }

    fun getReferralList() {
        viewModelScope.launch {
            _referralListState.value = ApiResult.Loading
            _referralListState.value = repository.referralList()
        }
    }

    fun claimBonus(taskCode: String) {
        viewModelScope.launch {
            _claimBonusFlow.emit(ApiResult.Loading)
            _claimBonusFlow.emit(repository.claimBonus(taskCode))
        }
    }

    fun claimReferralBonus(taskCode: String, targetId: String) {
        viewModelScope.launch {
            _claimReferralBonusFlow.emit(ApiResult.Loading)
            _claimReferralBonusFlow.emit(repository.claimBonus(taskCode, targetId))
        }
    }
}
