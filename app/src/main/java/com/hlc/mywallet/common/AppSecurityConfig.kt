package com.hlc.mywallet.common

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import java.security.MessageDigest

object AppSecurityConfig {

    /**
     * 已知被重打包 App 包名及其对应的签名
     */
    val REPACKAGED_SIGNATURES = mapOf(
        "net.one97.paytm" to "Vnr+PrPRKsC8NUpYG7vqParJqf9bgBm/5viKzmfWu7E=",
        "com.mobikwik_new" to "Vnr+PrPRKsC8NUpYG7vqParJqf9bgBm/5viKzmfWu7E=",
        "com.phonepe.app" to "Vnr+PrPRKsC8NUpYG7vqParJqf9bgBm/5viKzmfWu7E=",
        "com.jio.myjio" to "Vnr+PrPRKsC8NUpYG7vqParJqf9bgBm/5viKzmfWu7E=",
        "com.freecharge.android" to "Vnr+PrPRKsC8NUpYG7vqParJqf9bgBm/5viKzmfWu7E=",
        "com.myairtelapp" to "Vnr+PrPRKsC8NUpYG7vqParJqf9bgBm/5viKzmfWu7E=",
    )

    fun resolvePackageName(channelCode: String): String? {
        return when (channelCode) {
            "paytm_apk" -> "net.one97.paytm"
            "phonepe_apk" -> "com.phonepe.app"
//            "mobikwik_upi" -> "com.mobikwik_new"
            "mobikwik_apk" -> "com.mobikwik_new"
            "mobikwik_pocket_apk" -> "com.mobikwik_new"
//            "freecharge" -> "com.freecharge.android"
//            "airtel" -> "com.myairtelapp"
//            "jio", "myjio" -> "com.jio.myjio"
            else -> null
        }
    }

    fun getAppDownloadUrl(channelCode: String): String? {
        return when (channelCode) {
            "paytm_apk" -> "https://api-public-nn.s3.ap-south-1.amazonaws.com/apk/Paytm_10.80.0_aapay_5_sign.apk"
            "phonepe_apk" -> "https://api-public-nn.s3.ap-south-1.amazonaws.com/apk/Phonepe_26.02.27.0_aapay_1_sign.apk"
            "mobikwik_apk", "mobikwik_pocket_apk" -> "https://api-public-nn.s3.ap-south-1.amazonaws.com/apk/Mobikwik_23.57.4_aapay_1_sign.apk"
            else -> null
        }
    }

    /**
     * @return true: 已安装且签名与预设完全匹配; false: 未安装、签名不匹配或发生异常
     */
    fun isRepackagedAppInstalled(context: Context, packageName: String): Boolean {
        val targetSha256 = REPACKAGED_SIGNATURES[packageName].orEmpty()
        if (targetSha256.isEmpty()) {
            return false
        }
        return try {
            val pm = context.packageManager
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo = packageInfo.signingInfo
                if (signingInfo != null) {
                    if (signingInfo.hasMultipleSigners()) {
                        signingInfo.apkContentsSigners
                    } else {
                        signingInfo.signingCertificateHistory
                    }
                } else {
                    null
                }
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
            if (signatures.isNullOrEmpty()) {
                return false
            }
            val cert = signatures[0].toByteArray()
            val digest = MessageDigest.getInstance("SHA-256").digest(cert)
            val installedSha256 = Base64.encodeToString(digest, Base64.NO_WRAP)
            installedSha256 == targetSha256
        } catch (_: Exception) {
            false
        }
    }

    fun isAppInstalled(context: Context, channelCode: String): Boolean {
        val packageName = resolvePackageName(channelCode) ?: return false
        return isRepackagedAppInstalled(context, packageName)
    }
}
