package com.hlc.mywallet.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.hlc.lib_base.AppContext
import com.hlc.mywallet.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

/**
 * APK 下载、通知栏进度与应用内安装。
 */
object AppUpdateDownloader {

    private const val CHANNEL_ID = "app_update"
    private const val NOTIFICATION_ID = 0x1001
    private const val APK_FILE_NAME = "mywallet_update.apk"

    private val client = OkHttpClient.Builder().build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var downloadJob: Job? = null
    private var lastApkFile: File? = null

    private val _state = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val state: StateFlow<DownloadState> = _state.asStateFlow()

    sealed class DownloadState {
        data object Idle : DownloadState()
        data class Downloading(val progress: Int) : DownloadState()
        data class Success(val apkFile: File) : DownloadState()
        data class Failed(val message: String) : DownloadState()
    }

    fun startDownload(context: Context, apkUrl: String, versionName: String) {
        if (apkUrl.isBlank()) {
            _state.value = DownloadState.Failed(context.getString(R.string.invalid_download_url))
            return
        }
        val appContext = context.applicationContext
        cancel(notifyCancel = false)
        createNotificationChannel(appContext)
        showNotification(appContext, versionName, 0, indeterminate = true)

        downloadJob = scope.launch {
            _state.value = DownloadState.Downloading(0)
            try {
                val apkFile = downloadApk(appContext, apkUrl) { progress ->
                    _state.value = DownloadState.Downloading(progress)
                    showNotification(appContext, versionName, progress, indeterminate = false)
                }
                lastApkFile = apkFile
                _state.value = DownloadState.Success(apkFile)
                showNotification(appContext, versionName, 100, indeterminate = false, completed = true)
                installApk(appContext, apkFile)
            } catch (e: Exception) {
                _state.value = DownloadState.Failed(e.message ?: context.getString(R.string.download_failed))
                cancelNotification(appContext)
            }
        }
    }

    fun installApk(context: Context, apkFile: File? = lastApkFile) {
        val file = apkFile ?: return
        if (!file.exists()) return
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun cancel(notifyCancel: Boolean = true) {
        downloadJob?.cancel()
        downloadJob = null
        if (_state.value is DownloadState.Downloading) {
            _state.value = DownloadState.Idle
        }
        if (notifyCancel) {
            runCatching { cancelNotification(AppContext.get()) }
        }
    }

    private suspend fun downloadApk(
        context: Context,
        url: String,
        onProgress: (Int) -> Unit
    ): File {
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Download failed: ${response.code}")
        }
        val body = response.body ?: throw IllegalStateException("Empty response body")
        val totalBytes = body.contentLength()
        val apkDir = File(context.getExternalFilesDir(null), "apk").apply { mkdirs() }
        val apkFile = File(apkDir, APK_FILE_NAME)
        if (apkFile.exists()) {
            apkFile.delete()
        }

        body.byteStream().use { input ->
            FileOutputStream(apkFile).use { output ->
                val buffer = ByteArray(8 * 1024)
                var downloaded = 0L
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                    downloaded += read
                    if (totalBytes > 0L) {
                        val progress = ((downloaded * 100) / totalBytes).toInt().coerceIn(0, 100)
                        onProgress(progress)
                    }
                }
                output.flush()
            }
        }
        if (totalBytes <= 0L) {
            onProgress(100)
        }
        return apkFile
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.app_update_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        context.getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }

    private fun showNotification(
        context: Context,
        versionName: String,
        progress: Int,
        indeterminate: Boolean,
        completed: Boolean = false
    ) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val title = if (completed) {
            context.getString(R.string.app_update_download_complete)
        } else {
            context.getString(R.string.app_update_downloading, versionName)
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_security_shield)
            .setContentTitle(title)
            .setOnlyAlertOnce(true)
            .setOngoing(!completed)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (!completed) {
            builder.setProgress(100, progress, indeterminate)
        } else {
            builder.setProgress(0, 0, false)
        }
        manager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun cancelNotification(context: Context) {
        context.getSystemService(NotificationManager::class.java)
            ?.cancel(NOTIFICATION_ID)
    }

}
