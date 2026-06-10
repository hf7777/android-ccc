package com.hlc.mywallet.common

import androidx.fragment.app.FragmentActivity
import com.hlc.mywallet.data.model.resp.VersionResp
import com.hlc.mywallet.dialog.UpdateDialog

fun FragmentActivity.showUpdateDialog(version: VersionResp) {
    if (isFinishing || isDestroyed || supportFragmentManager.isStateSaved) {
        return
    }
    if (supportFragmentManager.findFragmentByTag(UpdateDialog.TAG) != null) {
        return
    }
    UpdateDialog.newInstance(version)
        .show(supportFragmentManager, UpdateDialog.TAG)
}
