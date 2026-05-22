package com.hlc.mywallet.data.model.resp

/**
 * @author Wade
 * @since 2026/5/16
 */
data class SliderCaptchaResp(
    val captchaId: String,
    val `data`: Data,
    val message: String,
    val result: Boolean,
    val token: String
) {
    data class Data(
        val bgBase64: String,
        val positionX: Int,
        val positionY: Int,
        val templateBase64: String,
        val templateCropH: Int,
        val templateCropW: Int,
        val templateOffsetX: Int,
        val templateOffsetY: Int
    )
}
