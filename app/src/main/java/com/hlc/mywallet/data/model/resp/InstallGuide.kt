package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Wade
 * @since 2026/5/27
 */
@Parcelize
data class InstallGuide(
    val content: String?,
    val imageUrl: String?,
    val module: String?,
    val status: String?,
    val title: String?
): Parcelable