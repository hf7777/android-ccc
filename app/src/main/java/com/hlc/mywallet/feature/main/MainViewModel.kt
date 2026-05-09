package com.hlc.mywallet.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.feature.mine.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _myWalletResultFlow = MutableSharedFlow<ApiResult<List<MyWalletResp>>>()
    val myWalletResultFlow: SharedFlow<ApiResult<List<MyWalletResp>>> = _myWalletResultFlow.asSharedFlow()

    fun getMyWallet() {
        viewModelScope.launch {
            val result = userRepository.getMyWallet()
            _myWalletResultFlow.emit(result)
        }
    }

    suspend fun getCachedMyWallet(): List<MyWalletResp>? {
        return userRepository.getCachedMyWallet()
    }
}
