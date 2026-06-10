package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.req.BankInfoReq
import com.hlc.mywallet.data.model.req.WalletBankInfoReq
import com.hlc.mywallet.data.model.resp.ApplyPermissionResp
import com.hlc.mywallet.data.model.resp.InstallGuide
import com.hlc.mywallet.data.model.resp.OtpActiveStatus
import com.hlc.mywallet.data.model.resp.PayChannelResp
import com.hlc.mywallet.data.model.resp.WalletBankInfoResp
import com.hlc.mywallet.data.model.resp.WalletListResp
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WalletService {

    /**
     * 钱包列表
     */
    @POST("/app/wallet/page")
    suspend fun getWalletList(@Body requestBody: RequestBody): BaseResponse<WalletListResp>

    /**
     * 开启提现
     */
    @POST("/app/wallet/startSelling")
    suspend fun startSelling(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 关闭提现
     */
    @POST("/app/wallet/stopSelling")
    suspend fun closeSelling(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 添加钱包时的钱包列表
     */
    @POST("/app/wallet/payChannelList")
    suspend fun payChannelList(@Body requestBody: RequestBody): BaseResponse<List<PayChannelResp>>

    /**
     * 获取授权状态
     */
    @POST("/app/wallet/query")
    suspend fun getPermissionStatus(@Body requestBody: RequestBody): BaseResponse<ApplyPermissionResp>

    /**
     * 获取授权状态
     */
    @POST("/app/wallet/login")
    suspend fun loginWallet(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 获取UPI列表
     */
    @POST("/app/wallet/getUpiList")
    suspend fun getUpiList(@Body requestBody: RequestBody): BaseResponse<List<String>>

    /**
     * 添加钱包
     */
    @POST("/app/wallet/add")
    suspend fun addWallet(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 编辑UPI
     */
    @POST("/app/wallet/editUpi")
    suspend fun editUpi(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 下线钱包
     */
    @POST("/app/wallet/deAuthorize")
    suspend fun deAuthorize(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 重新连接钱包
     */
    @POST("/app/wallet/relink")
    suspend fun relink(@Body requestBody: RequestBody): BaseResponse<Boolean>

    /**
     * 获取蚂蚁钱包版本
     */
    @GET("/app/wallet/walletVersion")
    suspend fun walletVersion(): BaseResponse<String>

    /**
     * 根据渠道编码获取教程
     */
    @GET("/app/wallet/moduleGuideList")
    suspend fun moduleGuideList(@Query("channelCode") channelCode: String): BaseResponse<List<InstallGuide>>

    /**
     * 获取辅助器激活状态
     */
    @POST("/app/wallet/autoActiveStatus")
    suspend fun autoActiveStatus(@Body requestBody: RequestBody): BaseResponse<OtpActiveStatus>

    /**
     * 添加银行卡
     */
    @POST("/app/bankCard/add")
    suspend fun addBankCard(@Body bankInfoReq: BankInfoReq): BaseResponse<String>

    /**
     * 编辑银行卡
     */
    @POST("/app/bankCard/edit")
    suspend fun editBankCard(@Body bankInfoReq: BankInfoReq): BaseResponse<String>

    /**
     * 查询银行卡列表
     */
    @GET("/app/walletBankCard/list")
    suspend fun walletBankCardList(@Query("walletId") walletId: String): BaseResponse<List<WalletBankInfoResp>>

    /**
     * 钱包银行卡详情
     */
    @GET("/app/walletBankCard/detail")
    suspend fun walletBankCardDetail(@Query("bankCardId") bankCardId: String): BaseResponse<WalletBankInfoResp>

    /**
     * 钱包添加银行卡
     */
    @POST("/app/walletBankCard/add")
    suspend fun addWalletBankCard(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 编辑钱包银行卡
     */
    @POST("/app/walletBankCard/edit")
    suspend fun editWalletBankCard(@Body requestBody: RequestBody): BaseResponse<String>

    /**
     * 触发UPI PIN校验
     */
    @POST("/app/walletBankCard/checkUpi")
    suspend fun checkUpiPin(@Body bankInfoReq: WalletBankInfoReq): BaseResponse<String>

}
