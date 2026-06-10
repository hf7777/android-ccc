package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Wade
 * @since 2026/5/22
 */
@Parcelize
data class VersionResp(
    val apkUrl: String,
    val forceUpdate: String,
    val versionCode: Int,
    val versionName: String
) : Parcelable