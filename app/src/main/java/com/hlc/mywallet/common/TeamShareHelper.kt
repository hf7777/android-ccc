package com.hlc.mywallet.common

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import com.hlc.mywallet.R
import java.io.File

/**
 * 团队邀请分享：图片 [R.drawable.share] + shareText。
 */
object TeamShareHelper {

    enum class ShareChannel {
        TELEGRAM,
        FACEBOOK,
        WHATSAPP,
        GENERIC
    }

    private const val AUTHORITY_SUFFIX = ".fileprovider"
    private const val CACHE_DIR_NAME = "share"
    private const val CACHE_FILE_NAME = "team_invite_share.png"

    private const val PKG_TELEGRAM = "org.telegram.messenger"
    private const val PKG_WHATSAPP = "com.whatsapp"
    private const val PKG_FACEBOOK_MESSENGER = "com.facebook.orca"
    private const val PKG_FACEBOOK = "com.facebook.katana"

    fun share(
        context: Context,
        shareText: String?,
        channel: ShareChannel,
        @DrawableRes imageRes: Int = R.drawable.share
    ): Boolean {
        val text = shareText.orEmpty()
        val imageUri = prepareShareImageUri(context, imageRes) ?: return false
        val intent = buildShareIntent(context, text, imageUri)

        return when (channel) {
            ShareChannel.TELEGRAM -> launchWithPackages(context, intent, imageUri, listOf(PKG_TELEGRAM))
            ShareChannel.WHATSAPP -> launchWithPackages(context, intent, imageUri, listOf(PKG_WHATSAPP))
            ShareChannel.FACEBOOK -> launchWithPackages(
                context,
                intent,
                imageUri,
                listOf(PKG_FACEBOOK_MESSENGER, PKG_FACEBOOK)
            )
            ShareChannel.GENERIC -> launchChooser(context, intent, imageUri)
        }
    }

    private fun buildShareIntent(context: Context, text: String, imageUri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            if (text.isNotEmpty()) {
                putExtra(Intent.EXTRA_TEXT, text)
            }
            clipData = ClipData.newUri(context.contentResolver, "share_image", imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun launchWithPackages(
        context: Context,
        baseIntent: Intent,
        imageUri: Uri,
        packages: List<String>
    ): Boolean {
        for (packageName in packages) {
            val targeted = Intent(baseIntent).setPackage(packageName)
            if (targeted.resolveActivity(context.packageManager) != null) {
                context.grantUriPermission(
                    packageName,
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                context.startActivity(targeted)
                return true
            }
        }
        return launchChooser(context, baseIntent, imageUri)
    }

    private fun launchChooser(context: Context, intent: Intent, imageUri: Uri): Boolean {
        val chooser = Intent.createChooser(intent, context.getString(R.string.share))
        grantUriPermissionToIntentHandlers(context, chooser, imageUri)
        if (chooser.resolveActivity(context.packageManager) == null) {
            return false
        }
        context.startActivity(chooser)
        return true
    }

    private fun grantUriPermissionToIntentHandlers(
        context: Context,
        intent: Intent,
        imageUri: Uri
    ) {
        val flags = PackageManager.MATCH_DEFAULT_ONLY
        val handlers = context.packageManager.queryIntentActivities(intent, flags)
        for (resolveInfo in handlers) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                imageUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    private fun prepareShareImageUri(
        context: Context,
        @DrawableRes imageRes: Int
    ): Uri? {
        return runCatching {
            val cacheDir = File(context.cacheDir, CACHE_DIR_NAME).apply { mkdirs() }
            val file = File(cacheDir, CACHE_FILE_NAME)
            if (!file.exists() || file.length() == 0L) {
                if (file.exists()) {
                    file.delete()
                }
                val bitmap = BitmapFactory.decodeResource(context.resources, imageRes)
                    ?: error("decode share image failed")
                file.outputStream().use { output ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                        error("compress share image failed")
                    }
                }
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
            FileProvider.getUriForFile(
                context,
                context.packageName + AUTHORITY_SUFFIX,
                file
            )
        }.getOrNull()
    }
}
