package com.hlc.mywallet.feature.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.data.model.resp.TutorialResp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor(
    private val repository: TutorialRepository
) : ViewModel() {

    private val _tutorialResultFlow = MutableSharedFlow<ApiResult<List<TutorialResp>>>()
    val tutorialResultFlow: SharedFlow<ApiResult<List<TutorialResp>>> =
        _tutorialResultFlow.asSharedFlow()

    fun getTutorials() {
        viewModelScope.launch {
            _tutorialResultFlow.emit(repository.getTutorials())
        }
    }
}
