package com.hlc.mywallet.feature.login

import android.content.Context
import com.hlc.lib_base.net.ApiResult
import com.hlc.lib_base.net.safeRequest
import com.hlc.mywallet.data.api.UserService
import com.hlc.mywallet.data.model.req.LoginReq
import com.hlc.mywallet.data.model.resp.CaptchaImage
import com.hlc.mywallet.data.model.resp.LoginResp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val userService: UserService
) {
    suspend fun captchaImage(): ApiResult<CaptchaImage> {
        return safeRequest {
            userService.captchaImage()
        }
    }

    suspend fun login(loginReq: LoginReq): ApiResult<LoginResp> {
        return safeRequest {
            userService.login(loginReq)
        }
    }
}
