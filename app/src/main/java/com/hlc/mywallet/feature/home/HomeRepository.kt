package com.hlc.mywallet.feature.home

import android.content.Context
import com.hlc.mywallet.R
import com.hlc.mywallet.feature.home.model.DemoItem
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.mywallet.data.api.MainService
import dagger.hilt.android.qualifiers.ApplicationContext
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val mainService: MainService
) {
    suspend fun loadDemo(): ApiResult<DemoItem> {
        LogUtils.d("Loading demo data...")
        return safeRequest {
            mainService.fetchMain()
        }.let { result ->
            when (result) {
                is ApiResult.Idle -> result
                is ApiResult.Success -> {
                    ApiResult.Success(
                        DemoItem(
                            title = StringUtils.getString(R.string.request_success),
                            subtitle = buildString {
                                appendLine("url=${result.data.url}")
                                appendLine("origin=${result.data.origin}")
                                appendLine("message=${result.data.message}")
                            }
                        )
                    )
                }
                is ApiResult.Error -> result
                is ApiResult.Loading -> result
            }
        }
    }
}
