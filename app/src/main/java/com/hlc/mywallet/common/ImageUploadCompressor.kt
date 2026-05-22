package com.hlc.mywallet.common

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

/**
 * 上传图片压缩工具。
 * 统一负责把本地图片压缩到指定大小以内，避免直接上传超大原图。
 */
object ImageUploadCompressor {

    const val DEFAULT_MAX_BYTES: Int = 1024 * 1024

    private const val DEFAULT_MAX_LONG_SIDE = 1920
    private const val DEFAULT_OUTPUT_MIME_TYPE = "image/jpeg"
    private const val INITIAL_JPEG_QUALITY = 95
    private const val MIN_JPEG_QUALITY = 20
    private const val JPEG_QUALITY_STEP = 5
    private const val SCALE_FACTOR = 0.85f

    /**
     * 把 [uri] 对应的图片压缩后转成 MultipartBody.Part，结果保证不超过 [maxBytes]。
     */
    suspend fun createMultipartPart(
        context: Context,
        uri: Uri,
        partName: String,
        maxBytes: Int = DEFAULT_MAX_BYTES
    ): MultipartBody.Part? = withContext(Dispatchers.IO) {
        val compressedBytes = compressImage(context, uri, maxBytes) ?: return@withContext null
        val requestBody = compressedBytes.toRequestBody(DEFAULT_OUTPUT_MIME_TYPE.toMediaTypeOrNull())
        MultipartBody.Part.createFormData(
            partName,
            buildOutputFileName(context.contentResolver, uri),
            requestBody
        )
    }

    private fun compressImage(
        context: Context,
        uri: Uri,
        maxBytes: Int
    ): ByteArray? {
        val sourceBytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val bitmap = decodeBitmap(sourceBytes) ?: return null
        return compressBitmapToTarget(bitmap, maxBytes)
    }

    private fun decodeBitmap(sourceBytes: ByteArray): Bitmap? {
        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(sourceBytes, 0, sourceBytes.size, boundsOptions)
        if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) {
            return null
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(boundsOptions.outWidth, boundsOptions.outHeight)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val decodedBitmap =
            BitmapFactory.decodeByteArray(sourceBytes, 0, sourceBytes.size, decodeOptions)
                ?: return null
        return scaleBitmapIfNeeded(decodedBitmap)
    }

    private fun compressBitmapToTarget(bitmap: Bitmap, maxBytes: Int): ByteArray? {
        var currentBitmap = bitmap
        var compressedBytes = compressByQuality(currentBitmap, maxBytes)

        while (compressedBytes.size > maxBytes && (currentBitmap.width > 1 || currentBitmap.height > 1)) {
            val nextBitmap = Bitmap.createScaledBitmap(
                currentBitmap,
                calculateNextSize(currentBitmap.width),
                calculateNextSize(currentBitmap.height),
                true
            )
            currentBitmap = nextBitmap
            compressedBytes = compressByQuality(currentBitmap, maxBytes)
        }

        return compressedBytes.takeIf { it.size <= maxBytes }
    }

    private fun compressByQuality(bitmap: Bitmap, maxBytes: Int): ByteArray {
        var quality = INITIAL_JPEG_QUALITY
        var result = bitmap.toJpegByteArray(quality)
        while (result.size > maxBytes && quality > MIN_JPEG_QUALITY) {
            quality -= JPEG_QUALITY_STEP
            result = bitmap.toJpegByteArray(quality)
        }
        return result
    }

    private fun Bitmap.toJpegByteArray(quality: Int): ByteArray {
        return ByteArrayOutputStream().use { outputStream ->
            compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.toByteArray()
        }
    }

    private fun calculateInSampleSize(width: Int, height: Int): Int {
        val maxSide = maxOf(width, height)
        if (maxSide <= DEFAULT_MAX_LONG_SIDE) {
            return 1
        }

        var sampleSize = 1
        var currentMaxSide = maxSide
        while (currentMaxSide > DEFAULT_MAX_LONG_SIDE) {
            sampleSize *= 2
            currentMaxSide /= 2
        }
        return sampleSize
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        val maxSide = maxOf(bitmap.width, bitmap.height)
        if (maxSide <= DEFAULT_MAX_LONG_SIDE) {
            return bitmap
        }

        val scale = DEFAULT_MAX_LONG_SIDE.toFloat() / maxSide.toFloat()
        val targetWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val targetHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun calculateNextSize(size: Int): Int {
        if (size <= 1) {
            return 1
        }
        return (size * SCALE_FACTOR).toInt().coerceIn(1, size - 1)
    }

    private fun buildOutputFileName(contentResolver: ContentResolver, uri: Uri): String {
        val defaultName = "upload_${System.currentTimeMillis()}.jpg"
        val displayName = runCatching {
            contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                } else {
                    null
                }
            }
        }.getOrNull()

        val baseName = displayName
            ?.substringBeforeLast('.', displayName)
            ?.takeIf { it.isNotBlank() }
            ?: uri.lastPathSegment
                ?.substringAfterLast('/')
                ?.substringBeforeLast('.', "")
                ?.takeIf { it.isNotBlank() }
            ?: return defaultName

        return "$baseName.jpg"
    }
}
