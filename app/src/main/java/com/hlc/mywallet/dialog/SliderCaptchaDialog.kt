package com.hlc.mywallet.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.setDrawablePadding
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.SliderCaptchaResp
import com.hlc.mywallet.databinding.DialogSliderCaptchaBinding
import kotlin.math.roundToInt

class SliderCaptchaDialog : DialogFragment() {

    private var _binding: DialogSliderCaptchaBinding? = null
    private val binding: DialogSliderCaptchaBinding get() = _binding!!

    private var captcha: SliderCaptchaResp? = null
    private var bgBitmap: Bitmap? = null
    private var pieceBitmap: Bitmap? = null
    private var onSuccessListener: ((captchaId: String, sliderPosition: String) -> Unit)? = null
    private var onRefreshListener: (() -> Unit)? = null

    private var touchOffsetX = 0f
    private var currentSliderPosition = 0f
    private var isDragging = false
    private var isVerifying = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            setCanceledOnTouchOutside(false)
            window?.apply {
                setBackgroundDrawableResource(R.color.transparent)
                setGravity(Gravity.CENTER)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSliderCaptchaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivClose.onClick { dismiss() }
        binding.btnCancel.onClick { dismiss() }
        binding.btnRefresh.setDrawablePadding(leftResId = R.drawable.ic_refresh_theme, leftPadding = 7.dp)
        binding.btnRefresh.onClick { onRefreshListener?.invoke() }
        setupSliderTouch()
        captcha?.let(::bindCaptcha)
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth = (resources.displayMetrics.widthPixels - 40.dp).coerceAtMost(350.dp)
        dialog?.window?.setLayout(
            dialogWidth,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnSuccessListener(listener: (captchaId: String, sliderPosition: String) -> Unit): SliderCaptchaDialog {
        onSuccessListener = listener
        return this
    }

    fun setOnRefreshListener(listener: () -> Unit): SliderCaptchaDialog {
        onRefreshListener = listener
        return this
    }

    fun updateCaptcha(captcha: SliderCaptchaResp) {
        this.captcha = captcha
        isVerifying = false
        isDragging = false
        if (_binding != null) {
            bindCaptcha(captcha)
        }
    }

    private fun bindCaptcha(captcha: SliderCaptchaResp) {
        bgBitmap = captcha.data.bgBase64.toBitmap()
        pieceBitmap = captcha.data.templateBase64.toBitmap()
        binding.ivBg.setImageBitmap(bgBitmap)
        binding.ivPiece.setImageBitmap(pieceBitmap)
        binding.flCaptcha.post {
            updatePieceSizeAndY(captcha)
            resetSlider()
        }
    }

    private fun setupSliderTouch() {
        binding.btnSlider.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (isVerifying) return@setOnTouchListener true
                    isDragging = true
                    touchOffsetX = event.rawX - view.x
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isDragging || isVerifying) return@setOnTouchListener true
                    val maxSliderX = maxSliderLeftPx().coerceAtMost(
                        (binding.flSliderTrack.width - view.width).toFloat()
                    ).coerceAtLeast(0f)
                    val nextX = (event.rawX - touchOffsetX).coerceIn(0f, maxSliderX)
                    updateSlider(nextX)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        isDragging = false
                        verifySlider()
                    }
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    isDragging = false
                    true
                }
                else -> false
            }
        }
    }

    private fun updatePieceSizeAndY(captcha: SliderCaptchaResp) {
        val scaleX = captchaScaleX()
        val scaleY = captchaScaleY()
        val pieceWidth = (captcha.data.templateCropW * scaleX).roundToInt().coerceAtLeast(1)
        val pieceHeight = (captcha.data.templateCropH * scaleY).roundToInt().coerceAtLeast(1)
        binding.ivPiece.layoutParams = binding.ivPiece.layoutParams.apply {
            width = pieceWidth
            height = pieceHeight
        }
        binding.btnSlider.layoutParams = binding.btnSlider.layoutParams.apply {
            width = pieceWidth
            height = pieceHeight
        }
        binding.flSliderTrack.layoutParams = binding.flSliderTrack.layoutParams.apply {
            height = pieceHeight
        }
        binding.ivPiece.y = captcha.data.templateOffsetY * scaleY
    }

    private fun updateSlider(sliderX: Float) {
        val captcha = captcha ?: return
        val scaleX = captchaScaleX()
        val sliderPosition = sliderX / scaleX
        val pieceX = (sliderPosition - captcha.data.positionX + captcha.data.templateOffsetX) * scaleX

        binding.btnSlider.x = sliderX
        binding.ivPiece.x = pieceX
        currentSliderPosition = sliderPosition
    }

    private fun verifySlider() {
        if (isVerifying) return
        val captcha = captcha ?: return
        isVerifying = true
        val sliderPosition = currentSliderPosition.roundToInt() + SLIDER_POSITION_OFFSET
        onSuccessListener?.invoke(captcha.captchaId, sliderPosition.toString())
    }

    private fun resetSlider() {
        isVerifying = false
        isDragging = false
        updateSlider(0f)
    }

    private fun maxSliderLeftPx(): Float {
        return (CAPTCHA_WIDTH - PUZZLE_SIZE - KNOB_HALF) * captchaScaleX()
    }

    private fun captchaScaleX(): Float {
        return binding.flCaptcha.width / CAPTCHA_WIDTH
    }

    private fun captchaScaleY(): Float {
        return binding.flCaptcha.height / CAPTCHA_HEIGHT
    }

    private fun String.toBitmap(): Bitmap? {
        return try {
            val pureBase64 = substringAfter("base64,", this)
            val bytes = Base64.decode(pureBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val CAPTCHA_WIDTH = 340f
        private const val CAPTCHA_HEIGHT = 170f
        private const val PUZZLE_SIZE = 44f
        private const val KNOB_HALF = 9f
        private const val SLIDER_POSITION_OFFSET = 11

        fun newInstance(captcha: SliderCaptchaResp): SliderCaptchaDialog {
            return SliderCaptchaDialog().apply {
                this.captcha = captcha
            }
        }
    }
}
