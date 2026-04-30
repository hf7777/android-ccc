package com.hlc.mywallet.feature.login

import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.Toaster
import com.hlc.lib_base.BaseVbActivity
import com.hlc.lib_base.extension.collectWithError
import com.hlc.lib_base.extension.enableWhenAllFilled
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.loadBase64
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.router.navigation
import com.hlc.lib_base.widget.hideLoading
import com.hlc.lib_base.widget.showLoading
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.req.LoginReq
import com.hlc.mywallet.databinding.ActivityLoginBinding
import com.hlc.mywallet.router.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseVbActivity<ActivityLoginBinding>() {

    override fun initImmersionBar() {
        immersionBar {
            statusBarColorInt(ColorUtils.getColor(R.color.theme))
            statusBarDarkFont(false)
            navigationBarColorInt(ColorUtils.getColor(R.color.theme))
            navigationBarDarkIcon(false)
            fitsSystemWindows(true)
        }
    }

    private val viewModel: LoginViewModel by viewModels()

    private var uuid: String? = ""

    override fun initView() {
        binding.pbLoading.visible()
        viewModel.captchaImage(true)

        binding.tvSignUp.onClick {
            navigation(Routes.REGISTER)
        }
        binding.ivCode.onClick {
            viewModel.captchaImage(true)
        }
        binding.btnSignIn.onClick {
            val myUUID = uuid
            if (myUUID.isNullOrEmpty()) {
                Toaster.show(getString(R.string.the_uuid_does_not_exist))
                return@onClick
            }

            val loginReq = LoginReq(
                username = binding.etPhoneNumber.text.toString().trim(),
                password = binding.etPassword.text.toString().trim(),
                code = binding.etCode.text.toString().trim(),
                uuid = myUUID
            )
            viewModel.login(loginReq)
        }
        
        // 监听输入框变化，控制登录按钮状态
        binding.btnSignIn.enableWhenAllFilled(
            binding.etPhoneNumber,
            binding.etPassword,
            binding.etCode
        )
    }

    override fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectWithError(
                        lifecycleOwner = this@LoginActivity,
                        onLoading = {
                            binding.pbLoading.visible()
                        },
                        onSuccess = { data->
                            binding.pbLoading.gone()
                            binding.ivCode.loadBase64(data.img)
                            uuid = data.uuid
                        },
                        onError = { errorMsg ->
                            binding.pbLoading.gone()
                            Toaster.show(errorMsg)
                        }
                    )
                }

                launch {
                    viewModel.LoginUiState.collectWithError(
                        lifecycleOwner = this@LoginActivity,
                        onLoading = {
                            showLoading()
                        },
                        onSuccess = {
                            navigation(Routes.MAIN)
                        },
                        onError = { errorMsg ->
                            hideLoading()
                            Toaster.show(errorMsg)
                        }
                    )
                }
            }
        }
    }
}