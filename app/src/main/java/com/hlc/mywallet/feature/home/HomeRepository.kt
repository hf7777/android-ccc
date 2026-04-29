package com.hlc.mywallet.feature.home

import android.content.Context
import com.hlc.mywallet.R
import com.hlc.mywallet.feature.home.model.DemoItem
import com.hlc.lib_base.ApiResult
import com.hlc.lib_base.safeRequest
import com.hlc.lib_net.MainService
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val mainService: MainService,
    @ApplicationContext private val context: Context
) {
    suspend fun loadDemo(): ApiResult<DemoItem> {
        Timber.d("Loading demo data...")
        return safeRequest(context) {
            mainService.fetchMain()
        }.let { result ->
            when (result) {
                is ApiResult.Success -> {
                    ApiResult.Success(
                        DemoItem(
                            title = context.getString(R.string.request_success),
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
