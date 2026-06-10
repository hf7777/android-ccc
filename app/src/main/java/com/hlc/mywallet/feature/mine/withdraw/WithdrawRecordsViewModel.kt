package com.hlc.mywallet.feature.mine.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.WithdrawRecordResp
import com.hlc.mywallet.feature.mine.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawRecordsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _withdrawRecordsFlow = MutableSharedFlow<ApiResult<WithdrawRecordResp>>()
    val withdrawRecordsFlow: SharedFlow<ApiResult<WithdrawRecordResp>> = _withdrawRecordsFlow.asSharedFlow()

    fun getWithdrawRecords(page: Int, pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch {
            _withdrawRecordsFlow.emit(ApiResult.Loading)
            _withdrawRecordsFlow.emit(repository.getWithdrawRecords(page, pageSize))
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
