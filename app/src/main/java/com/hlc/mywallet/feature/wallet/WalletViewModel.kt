package com.hlc.mywallet.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.StringUtils
import com.hlc.lib_base.extension.buildJsonBody
import com.hlc.lib_base.net.ApiException
import com.hlc.lib_base.net.ApiResult
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.ApplyPermissionResp
import com.hlc.mywallet.data.model.resp.CheckBindingResp
import com.hlc.mywallet.data.model.resp.InstallGuide
import com.hlc.mywallet.data.model.resp.OtpActiveStatus
import com.hlc.mywallet.data.model.resp.PayChannelResp
import com.hlc.mywallet.data.model.req.WalletBankInfoReq
import com.hlc.mywallet.data.model.resp.WalletBankInfoResp
import com.hlc.mywallet.data.model.resp.WalletListResp
import com.hlc.mywallet.feature.mine.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    data class WalletSellAction(
        val phone: String,
        val channelCode: String
    )

    private val _walletListState = MutableSharedFlow<ApiResult<WalletListResp>>()
    val walletListState = _walletListState.asSharedFlow()
    private val _walletSellState = MutableSharedFlow<ApiResult<WalletSellAction>>()
    val walletSellState = _walletSellState.asSharedFlow()
    private val _payChannelListState = MutableSharedFlow<ApiResult<List<PayChannelResp>>>()
    val payChannelListState = _payChannelListState.asSharedFlow()
    private val _upiListFlow = MutableSharedFlow<ApiResult<List<String>>>()
    val upiListFlow: SharedFlow<ApiResult<List<String>>> = _upiListFlow.asSharedFlow()

    private val _checkBindingFlow = MutableSharedFlow<ApiResult<CheckBindingResp>>()
    val checkBindingFlow: SharedFlow<ApiResult<CheckBindingResp>> = _checkBindingFlow.asSharedFlow()
    private val _applyPermissionFlow = MutableSharedFlow<ApiResult<ApplyPermissionResp>>()
    val applyPermissionFlow: SharedFlow<ApiResult<ApplyPermissionResp>> = _applyPermissionFlow.asSharedFlow()
    private val _loginWalletFlow = MutableSharedFlow<ApiResult<Unit>>()
    val loginWalletFlow: SharedFlow<ApiResult<Unit>> = _loginWalletFlow.asSharedFlow()
    private val _addWalletFlow = MutableSharedFlow<ApiResult<Unit>>()
    val addWalletFlow: SharedFlow<ApiResult<Unit>> = _addWalletFlow.asSharedFlow()
    private val _editUpiFlow = MutableSharedFlow<ApiResult<Unit>>()
    val editUpiFlow: SharedFlow<ApiResult<Unit>> = _editUpiFlow.asSharedFlow()
    private val _deAuthorizeFlow = MutableSharedFlow<ApiResult<Unit>>()
    val deAuthorizeFlow: SharedFlow<ApiResult<Unit>> = _deAuthorizeFlow.asSharedFlow()
    private val _relinkFlow = MutableSharedFlow<ApiResult<Boolean>>()
    val relinkFlow: SharedFlow<ApiResult<Boolean>> = _relinkFlow.asSharedFlow()
    private val _walletVersionFlow = MutableSharedFlow<ApiResult<String>>()
    val walletVersionFlow: SharedFlow<ApiResult<String>> = _walletVersionFlow.asSharedFlow()
    private val _moduleGuideListFlow = MutableSharedFlow<ApiResult<List<InstallGuide>>>()
    val moduleGuideListFlow: SharedFlow<ApiResult<List<InstallGuide>>> = _moduleGuideListFlow.asSharedFlow()
    private val _autoActiveStatusFlow = MutableSharedFlow<ApiResult<OtpActiveStatus>>()
    val autoActiveStatusFlow: SharedFlow<ApiResult<OtpActiveStatus>> = _autoActiveStatusFlow.asSharedFlow()

    private val _walletBankCardFlow = MutableSharedFlow<ApiResult<Unit>>()
    val walletBankCardFlow: SharedFlow<ApiResult<Unit>> = _walletBankCardFlow.asSharedFlow()
    private val _walletBankCardListFlow = MutableSharedFlow<ApiResult<List<WalletBankInfoResp>>>()
    val walletBankCardListFlow: SharedFlow<ApiResult<List<WalletBankInfoResp>>> =
        _walletBankCardListFlow.asSharedFlow()

    fun getWalletList(page: Int, pageSize: Int = 20) {
        viewModelScope.launch {
            val params = buildJsonBody(
                "pageNum" to page.toString(),
                "pageSize" to pageSize.toString(),
                "timezone" to "IN"
            )
            _walletListState.emit(ApiResult.Loading)
            _walletListState.emit(repository.getWalletList(params))
        }
    }

    fun startSelling(phone: String, channelCode: String) {
        viewModelScope.launch {
            _walletSellState.emit(ApiResult.Loading)
            val result = repository.startSelling(phone, channelCode)
            when (result) {
                is ApiResult.Success -> {
                    _walletSellState.emit(
                        ApiResult.Success(
                            WalletSellAction(phone = phone, channelCode = channelCode)
                        )
                    )
                }

                is ApiResult.Error -> _walletSellState.emit(result)
                else -> Unit
            }
        }
    }

    fun closeSelling(phone: String, channelCode: String) {
        viewModelScope.launch {
            _walletSellState.emit(ApiResult.Loading)
            val result = repository.closeSelling(phone, channelCode)
            when (result) {
                is ApiResult.Success -> {
                    _walletSellState.emit(
                        ApiResult.Success(
                            WalletSellAction(phone = phone, channelCode = channelCode)
                        )
                    )
                }

                is ApiResult.Error -> _walletSellState.emit(result)
                else -> Unit
            }
        }
    }

    fun payChannelList(sellStatus: String = "", buyStatus: String = "", autoBuyStatus: String = "") {
        viewModelScope.launch {
            _payChannelListState.emit(ApiResult.Loading)
            _payChannelListState.emit(
                repository.payChannelList(buyStatus = buyStatus, sellStatus = sellStatus, autoBuyStatus = autoBuyStatus)
            )
        }
    }

    fun getUpiList(phone: String, channelCode: String) {
        viewModelScope.launch {
            _upiListFlow.emit(ApiResult.Loading)
            _upiListFlow.emit(repository.getUpiList(phone, channelCode))
        }
    }

    fun addWallet(phone: String, channelCode: String, otp: String, pin: String, upiId: String) {
        viewModelScope.launch {
            _addWalletFlow.emit(ApiResult.Loading)
            _addWalletFlow.emit(repository.addWallet(phone, channelCode, otp, pin, upiId))
        }
    }

    fun editUpi(phone: String, channelCode: String, newUpi: String) {
        viewModelScope.launch {
            _editUpiFlow.emit(ApiResult.Loading)
            _editUpiFlow.emit(repository.editUpi(phone, channelCode, newUpi))
        }
    }

    fun deAuthorize(phone: String, channelCode: String) {
        viewModelScope.launch {
            _deAuthorizeFlow.emit(ApiResult.Loading)
            _deAuthorizeFlow.emit(repository.deAuthorize(phone, channelCode))
        }
    }

    fun relink(phone: String, channelCode: String) {
        viewModelScope.launch {
            _relinkFlow.emit(ApiResult.Loading)
            _relinkFlow.emit(repository.relink(phone, channelCode))
        }
    }

    fun walletVersion() {
        viewModelScope.launch {
            _walletVersionFlow.emit(ApiResult.Loading)
            _walletVersionFlow.emit(repository.walletVersion())
        }
    }

    fun moduleGuideList(channelCode: String) {
        viewModelScope.launch {
            _moduleGuideListFlow.emit(ApiResult.Loading)
            _moduleGuideListFlow.emit(repository.moduleGuideList(channelCode))
        }
    }

    fun autoActiveStatus(phone: String, channelCode: String) {
        viewModelScope.launch {
            _autoActiveStatusFlow.emit(repository.autoActiveStatus(phone, channelCode))
        }
    }

    fun getWalletBankCardList(walletId: String) {
        viewModelScope.launch {
            _walletBankCardListFlow.emit(ApiResult.Loading)
            _walletBankCardListFlow.emit(repository.walletBankCardList(walletId))
        }
    }

    fun checkUpiPinAndAddWalletBankCard(req: WalletBankInfoReq, isEditMode: Boolean) {
        viewModelScope.launch {
            _walletBankCardFlow.emit(ApiResult.Loading)
            when (val checkResult = repository.checkUpiPin(req)) {
                is ApiResult.Success -> {
                    val bankCardId = checkResult.data
                    when (val pollResult = pollBankCardUntilVerified(bankCardId)) {
                        is ApiResult.Success -> {
                            if (isEditMode) {
                                _walletBankCardFlow.emit(repository.editWalletBankCard(bankCardId))
                            } else {
                                _walletBankCardFlow.emit(repository.addWalletBankCard(bankCardId))
                            }
                        }
                        is ApiResult.Error -> {
                            _walletBankCardFlow.emit(pollResult)
                        }
                        else -> Unit
                    }
                }
                is ApiResult.Error -> {
                    _walletBankCardFlow.emit(checkResult)
                }
                else -> Unit
            }
        }
    }

    private suspend fun pollBankCardUntilVerified(bankCardId: String): ApiResult<Unit> {
        while (true) {
            when (val detailResult = repository.walletBankCardDetail(bankCardId)) {
                is ApiResult.Success -> {
                    if (detailResult.data.status == BANK_CARD_STATUS_VERIFIED) {
                        return ApiResult.Success(Unit)
                    } else if (detailResult.data.status == BANK_CARD_STATUS_FAILED) {
                        return ApiResult.Error(
                            ApiException.BusinessException(
                                -1,
                                detailResult.data.errorMsg?: StringUtils.getString(R.string.error_network)
                            )
                        )
                    }
                    delay(BANK_CARD_VERIFY_POLL_INTERVAL_MILLIS)
                }
                is ApiResult.Error -> return detailResult
                else -> delay(BANK_CARD_VERIFY_POLL_INTERVAL_MILLIS)
            }
        }
    }


    fun checkBinding() {
        viewModelScope.launch {
            _checkBindingFlow.emit(ApiResult.Loading)
            _checkBindingFlow.emit(userRepository.checkBinding())
        }
    }

    fun loginAndGetPermissionStatus(phone: String, channelCode: String, otp: String) {
        viewModelScope.launch {
            _applyPermissionFlow.emit(ApiResult.Loading)

            when (val loginResult = repository.loginWallet(phone, channelCode, otp)) {
                is ApiResult.Success -> Unit
                is ApiResult.Error -> {
                    _loginWalletFlow.emit(loginResult)
                    return@launch
                }
                else -> return@launch
            }

            // 登录成功后轮询授权状态
            var hasEmittedApplySuccess = false
            while (true) {
                when (val applyResult = repository.getPermissionStatus(phone, channelCode)) {
                    is ApiResult.Success -> {
                        if (!hasEmittedApplySuccess) {
                            _applyPermissionFlow.emit(applyResult)
                            hasEmittedApplySuccess = true
                        }
                        when (applyResult.data.authStatus) {
                            AUTH_STATUS_FAILED -> {
                                _applyPermissionFlow.emit(
                                    ApiResult.Error(
                                        ApiException.BusinessException(
                                            AUTH_STATUS_FAILED.toIntOrNull() ?: 5,
                                            applyResult.data.errorMsg ?: ""
                                        )
                                    )
                                )
                                break
                            }
                            AUTH_STATUS_SUCCESS -> {
                                _loginWalletFlow.emit(ApiResult.Success(Unit))
                                break
                            }
                            else -> delay(APPLY_PERMISSION_POLL_INTERVAL_MILLIS)
                        }
                    }

                    is ApiResult.Error -> {
                        _applyPermissionFlow.emit(applyResult)
                        break
                    }

                    else -> Unit
                }
            }
        }
    }


    companion object {
        private const val AUTH_STATUS_SUCCESS = "3"
        private const val AUTH_STATUS_FAILED = "5"
        private const val APPLY_PERMISSION_POLL_INTERVAL_MILLIS = 1_000L
        private const val BANK_CARD_STATUS_VERIFIED = "verified"
        private const val BANK_CARD_STATUS_FAILED = "failed"
        private const val BANK_CARD_VERIFY_POLL_INTERVAL_MILLIS = 1_000L
    }
}
