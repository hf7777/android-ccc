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
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hlc.lib_base.R

/**
 * 加载 Base64 编码的图片
 */
fun ImageView.loadBase64(
    base64String: String?,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
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

/**
 * 正常加载图片
 */
fun ImageView.load(
    url: Any?,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this.context)
        .load(url)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(this)
}

/**
 * 加载圆角图片
 * @param url 图片地址（支持 String、Uri、File、Drawable、Bitmap 等）
 * @param radius 圆角半径（单位：dp）
 * @param placeholder 占位图
 * @param error 错误图
 */
fun ImageView.loadRounded(
    url: Any?,
    radius: Int = 8,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    val radiusPx = (radius * context.resources.displayMetrics.density).toInt()
    val transformation = MultiTransformation<Bitmap>(
        CenterCrop(),
        RoundedCorners(radiusPx)
    )
    
    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions.bitmapTransform(transformation))
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(this)
}

/**
 * 加载圆形图片
 * @param url 图片地址
 * @param placeholder 占位图
 * @param error 错误图
 */
fun ImageView.loadCircle(
    url: Any?,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(this)
}

/**
 * 加载带边框的圆形图片
 * @param url 图片地址
 * @param borderWidth 边框宽度（单位：dp）
 * @param borderColor 边框颜色
 * @param placeholder 占位图
 * @param error 错误图
 */
fun ImageView.loadCircleWithBorder(
    url: Any?,
    borderWidth: Int = 2,
    borderColor: Int = android.graphics.Color.WHITE,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    val borderWidthPx = (borderWidth * context.resources.displayMetrics.density).toInt()
    val imageView = this
    
    Glide.with(this.context)
        .asBitmap()
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val borderedBitmap = addBorderToBitmap(resource, borderWidthPx, borderColor)
                imageView.setImageBitmap(borderedBitmap)
            }
            
            override fun onLoadCleared(placeholder: Drawable?) {
                imageView.setImageDrawable(placeholder)
            }
        })
}

/**
 * 加载带边框的圆角图片
 * @param url 图片地址
 * @param radius 圆角半径（单位：dp）
 * @param borderWidth 边框宽度（单位：dp）
 * @param borderColor 边框颜色
 * @param placeholder 占位图
 * @param error 错误图
 */
fun ImageView.loadRoundedWithBorder(
    url: Any?,
    radius: Int = 8,
    borderWidth: Int = 2,
    borderColor: Int = android.graphics.Color.WHITE,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0
) {
    val radiusPx = (radius * context.resources.displayMetrics.density).toInt()
    val borderWidthPx = (borderWidth * context.resources.displayMetrics.density).toInt()
    val transformation = MultiTransformation<Bitmap>(
        CenterCrop(),
        RoundedCorners(radiusPx)
    )
    val imageView = this
    
    Glide.with(this.context)
        .asBitmap()
        .load(url)
        .apply(RequestOptions.bitmapTransform(transformation))
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition:Transition<in Bitmap>?) {
                val borderedBitmap = addBorderToBitmap(resource, borderWidthPx, borderColor, radiusPx.toFloat())
                imageView.setImageBitmap(borderedBitmap)
            }
            
            override fun onLoadCleared(placeholder: Drawable?) {
                imageView.setImageDrawable(placeholder)
            }
        })
}

/**
 * 清除图片加载
 */
fun ImageView.clearImage() {
    Glide.with(this.context).clear(this)
}

/**
 * 给 Bitmap 添加圆形边框
 */
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
    
    // 绘制边框圆
    val centerX = (size + borderWidth * 2) / 2f
    val centerY = (size + borderWidth * 2) / 2f
    val radius = size / 2f + borderWidth
    canvas.drawCircle(centerX, centerY, radius, paint)
    
    // 绘制图片
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    val rect = Rect(borderWidth, borderWidth, size + borderWidth, size + borderWidth)
    canvas.drawBitmap(bitmap, null, rect, paint)
    
    return output
}

/**
 * 给 Bitmap 添加圆角边框
 */
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
    
    // 绘制边框圆角矩形
    val rectF = android.graphics.RectF(0f, 0f, width.toFloat(), height.toFloat())
    canvas.drawRoundRect(rectF, cornerRadius + borderWidth, cornerRadius + borderWidth, paint)
    
    // 绘制图片
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    val rect = Rect(borderWidth, borderWidth, bitmap.width + borderWidth, bitmap.height + borderWidth)
    canvas.drawBitmap(bitmap, null, rect, paint)
    
    return output
}
