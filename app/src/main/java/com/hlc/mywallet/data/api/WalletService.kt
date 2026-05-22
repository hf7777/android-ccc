package com.hlc.mywallet.data.api

import com.hlc.lib_base.net.BaseResponse
import com.hlc.mywallet.data.model.resp.ApplyPermissionResp
import com.hlc.mywallet.data.model.resp.PayChannelResp
import com.hlc.mywallet.data.model.resp.WalletListResp
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

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

}
