package com.hlc.mywallet.common

import com.hlc.mywallet.data.model.resp.VersionResp

sealed interface AppUpdateCheckEvent {
    data class HasUpdate(val version: VersionResp) : AppUpdateCheckEvent
    data object UpToDate : AppUpdateCheckEvent
    data class Failed(val message: String) : AppUpdateCheckEvent
}
