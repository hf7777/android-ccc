package com.hlc.mywallet.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TutorialResp(
    val content: String?,
    val coverImage: String?,
    val videoUrl: String?,
    val createBy: Int?,
    val id: String?,
    val sort: String?,
    val status: String?,
    val subtitle: String?,
    val title: String?,
    val updateBy: Int?,
    val updateTime: String?
): Parcelable