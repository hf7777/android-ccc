package com.hlc.lib_base.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Base64
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.hlc.lib_base.R

/**
 * 统一图片加载：支持 [ImageView.ScaleType]、圆角（dp）、圆形裁剪。
 */
fun ImageView.loadImage(
    url: Any?,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP,
    radiusDp: Int = 0,
    isCircle: Boolean = false,
    @DrawableRes placeholder: Int = R.drawable.bg_image_placeholder,
    @DrawableRes error: Int = R.drawable.bg_image_placeholder
) {
    this.scaleType = scaleType
    Glide.with(context)
        .load(url)
        .apply(
            buildImageRequestOptions(context, scaleType, radiusDp, isCircle)
                .applyPlaceholders(placeholder, error)
        )
        .into(this)
}

/**
 * 加载 Base64 编码的图片
 */
fun ImageView.loadBase64(
    base64String: String?,
    @DrawableRes placeholder: Int = R.drawable.bg_image_placeholder,
    @DrawableRes error: Int = R.drawable.bg_image_placeholder
) {
    if (placeholder != 0) {
        setImageResource(placeholder)
    }
    if (base64String.isNullOrEmpty()) {
        if (error != 0) setImageResource(error)
        return
    }

    try {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        setImageBitmap(bitmap)
    } catch (e: Exception) {
        if (error != 0) setImageResource(error)
    }
}

fun ImageView.load(
    url: Any?,
    @DrawableRes placeholder: Int = R.drawable.bg_image_placeholder,
    @DrawableRes error: Int = R.drawable.bg_image_placeholder,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
) {
    loadImage(url, scaleType, radiusDp = 0, isCircle = false, placeholder = placeholder, error = error)
}

fun ImageView.loadRounded(
    url: Any?,
    radius: Int = 8,
    @DrawableRes placeholder: Int = R.drawable.bg_image_placeholder,
    @DrawableRes error: Int = R.drawable.bg_image_placeholder,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
) {
    loadImage(url, scaleType, radiusDp = radius, isCircle = false, placeholder = placeholder, error = error)
}

fun ImageView.loadCircle(
    url: Any?,
    @DrawableRes placeholder: Int = R.drawable.bg_image_placeholder_circle,
    @DrawableRes error: Int = R.drawable.bg_image_placeholder_circle,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
) {
    loadImage(url, scaleType, radiusDp = 0, isCircle = true, placeholder = placeholder, error = error)
}

fun ImageView.loadCircleWithBorder(
    url: Any?,
    borderWidth: Int = 2,
    borderColor: Int = android.graphics.Color.WHITE,
    @DrawableRes placeholder: Int = R.drawable.bg_image_placeholder,
    @DrawableRes error: Int = R.drawable.bg_image_placeholder,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
) {
    this.scaleType = scaleType
    val borderWidthPx = (borderWidth * context.resources.displayMetrics.density).toInt()
    val imageView = this

    Glide.with(context)
        .asBitmap()
        .load(url)
        .apply(
            buildImageRequestOptions(context, scaleType, radiusDp = 0, isCircle = true)
                .applyPlaceholders(placeholder, error)
        )
        .into(object : CustomViewTarget<ImageView, Bitmap>(imageView) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                imageView.setImageBitmap(addBorderToBitmap(resource, borderWidthPx, borderColor))
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                imageView.setImageDrawable(errorDrawable)
            }

            override fun onResourceCleared(placeholder: Drawable?) {
                imageView.setImageDrawable(placeholder)
            }
        })
}

fun ImageView.loadRoundedWithBorder(
    url: Any?,
    radius: Int = 8,
    borderWidth: Int = 2,
    borderColor: Int = android.graphics.Color.WHITE,
    @DrawableRes placeholder: Int = R.drawable.bg_image_placeholder,
    @DrawableRes error: Int = R.drawable.bg_image_placeholder,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
) {
    this.scaleType = scaleType
    val borderWidthPx = (borderWidth * context.resources.displayMetrics.density).toInt()
    val imageView = this

    Glide.with(context)
        .asBitmap()
        .load(url)
        .apply(
            buildImageRequestOptions(context, scaleType, radiusDp = radius, isCircle = false)
                .applyPlaceholders(placeholder, error)
        )
        .into(object : CustomViewTarget<ImageView, Bitmap>(imageView) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val radiusPx = (radius * imageView.context.resources.displayMetrics.density).toInt()
                imageView.setImageBitmap(
                    addBorderToBitmap(resource, borderWidthPx, borderColor, radiusPx.toFloat())
                )
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                imageView.setImageDrawable(errorDrawable)
            }

            override fun onResourceCleared(placeholder: Drawable?) {
                imageView.setImageDrawable(placeholder)
            }
        })
}

fun ImageView.clearImage() {
    Glide.with(context).clear(this)
}

private fun buildImageRequestOptions(
    context: android.content.Context,
    scaleType: ImageView.ScaleType,
    radiusDp: Int,
    isCircle: Boolean
): RequestOptions {
    val scaleTransform = scaleType.toGlideTransform()
    return when {
        isCircle -> RequestOptions().transform(scaleTransform, CircleCrop())
        radiusDp > 0 -> {
            val radiusPx = (radiusDp * context.resources.displayMetrics.density).toInt()
            RequestOptions().transform(scaleTransform, RoundedCorners(radiusPx))
        }
        else -> RequestOptions().transform(scaleTransform)
    }
}

private fun ImageView.ScaleType.toGlideTransform(): Transformation<Bitmap> {
    return when (this) {
        ImageView.ScaleType.FIT_CENTER,
        ImageView.ScaleType.FIT_START,
        ImageView.ScaleType.FIT_END -> FitCenter()
        ImageView.ScaleType.CENTER_INSIDE -> CenterInside()
        ImageView.ScaleType.CENTER_CROP,
        ImageView.ScaleType.FIT_XY,
        ImageView.ScaleType.CENTER,
        ImageView.ScaleType.MATRIX -> CenterCrop()
    }
}

private fun RequestOptions.applyPlaceholders(
    @DrawableRes placeholder: Int,
    @DrawableRes error: Int
): RequestOptions {
    var options = this
    if (placeholder != 0) options = options.placeholder(placeholder)
    if (error != 0) options = options.error(error)
    return options
}

private fun addBorderToBitmap(
    bitmap: Bitmap,
    borderWidth: Int,
    borderColor: Int
): Bitmap {
    val size = bitmap.width.coerceAtLeast(bitmap.height)
    val output = Bitmap.createBitmap(size + borderWidth * 2, size + borderWidth * 2, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint().apply {
        isAntiAlias = true
        color = borderColor
    }

    val centerX = (size + borderWidth * 2) / 2f
    val centerY = (size + borderWidth * 2) / 2f
    val radius = size / 2f + borderWidth
    canvas.drawCircle(centerX, centerY, radius, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    val rect = Rect(borderWidth, borderWidth, size + borderWidth, size + borderWidth)
    canvas.drawBitmap(bitmap, null, rect, paint)

    return output
}

private fun addBorderToBitmap(
    bitmap: Bitmap,
    borderWidth: Int,
    borderColor: Int,
    cornerRadius: Float
): Bitmap {
    val width = bitmap.width + borderWidth * 2
    val height = bitmap.height + borderWidth * 2
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint().apply {
        isAntiAlias = true
        color = borderColor
    }

    val rectF = android.graphics.RectF(0f, 0f, width.toFloat(), height.toFloat())
    canvas.drawRoundRect(rectF, cornerRadius + borderWidth, cornerRadius + borderWidth, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    val rect = Rect(borderWidth, borderWidth, bitmap.width + borderWidth, bitmap.height + borderWidth)
    canvas.drawBitmap(bitmap, null, rect, paint)

    return output
}
