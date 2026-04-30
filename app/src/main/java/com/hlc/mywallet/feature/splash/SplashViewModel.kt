package com.hlc.mywallet.feature.splash

import androidx.lifecycle.ViewModel
import com.hlc.mywallet.manager.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {
    
    /**
     * 检查登录状态
     */
    suspend fun checkLoginStatus(): Boolean {
        return userManager.isLoggedIn()
    }
}
