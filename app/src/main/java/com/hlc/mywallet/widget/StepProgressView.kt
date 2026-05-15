package com.hlc.mywallet.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.hlc.mywallet.R
import kotlin.math.max

class StepProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val stepTitles = mutableListOf("Phone", "Authorize", "Finish")
    private var currentStep = 1

    private val activeColor = Color.BLACK
    private val inactiveCircleColor = 0xFFD9D9D9.toInt()
    private val inactiveLineColor = 0xFFE6E6E6.toInt()
    private val activeTextColor = Color.WHITE
    private val inactiveNumberColor = Color.WHITE
    private val activeTitleColor = Color.BLACK
    private val inactiveTitleColor = 0xFFCFCFCF.toInt()

    private val circleRadius = dp(15f)
    private val innerRingRadius = dp(11f)
    private val lineThickness = dp(1.5f)
    private val topPaddingSize = dp(4f)
    private val titleTopSpacing = dp(12f)
    private val contentHorizontalPadding = dp(8f)
    private val lineGap = dp(10f)

    private val circleFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1.5f)
        color = Color.WHITE
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = lineThickness
    }
    private val numberPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = sp(14f)
        isFakeBoldText = true
    }
    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = sp(14f)
    }

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.StepProgressView).apply {
                getString(R.styleable.StepProgressView_stepTitles)
                    ?.split("|")
                    ?.map { title -> title.trim() }
                    ?.filter { title -> title.isNotEmpty() }
                    ?.take(STEP_COUNT)
                    ?.let { titles ->
                        if (titles.isNotEmpty()) {
                            stepTitles.clear()
                            stepTitles.addAll(titles)
                            while (stepTitles.size < STEP_COUNT) {
                                stepTitles.add((stepTitles.size + 1).toString())
                            }
                        }
                    }
                currentStep = getInt(R.styleable.StepProgressView_currentStep, 1).coerceIn(1, STEP_COUNT)
                recycle()
            }
        }
    }

    fun setCurrentStep(step: Int) {
        val newStep = step.coerceIn(1, STEP_COUNT)
        if (currentStep == newStep) return
        currentStep = newStep
        invalidate()
    }

    fun setStepTitles(vararg titles: String) {
        setStepTitles(titles.toList())
    }

    fun setStepTitles(titles: List<String>) {
        if (titles.isEmpty()) return
        stepTitles.clear()
        stepTitles.addAll(titles.take(STEP_COUNT))
        while (stepTitles.size < STEP_COUNT) {
            stepTitles.add((stepTitles.size + 1).toString())
        }
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = paddingLeft + paddingRight + dp(280f).toInt()
        val titleHeight = (titlePaint.fontMetrics.bottom - titlePaint.fontMetrics.top)
        val desiredHeight = (
            paddingTop + topPaddingSize + circleRadius * 2 + titleTopSpacing + titleHeight + paddingBottom
            ).toInt()

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val firstTitleHalfWidth = titlePaint.measureText(stepTitles.firstOrNull().orEmpty()) / 2f
        val lastTitleHalfWidth = titlePaint.measureText(stepTitles.lastOrNull().orEmpty()) / 2f
        val startX = paddingLeft + max(contentHorizontalPadding, max(circleRadius, firstTitleHalfWidth))
        val endX = width - paddingRight - max(contentHorizontalPadding, max(circleRadius, lastTitleHalfWidth))
        val availableWidth = (endX - startX).coerceAtLeast(0f)
        val segmentWidth = availableWidth / (STEP_COUNT - 1)
        val centerY = paddingTop + topPaddingSize + circleRadius
        val centers = List(STEP_COUNT) { index -> startX + segmentWidth * index }

        drawLines(canvas, centers, centerY)
        centers.forEachIndexed { index, centerX ->
            drawStep(canvas, centerX, centerY, index)
        }
        centers.forEachIndexed { index, centerX ->
            drawTitle(canvas, centerX, centerY, index)
        }
    }

    private fun drawLines(canvas: Canvas, centers: List<Float>, centerY: Float) {
        for (index in 0 until STEP_COUNT - 1) {
            linePaint.color = if (index + 1 < currentStep) activeColor else inactiveLineColor
            canvas.drawLine(
                centers[index] + circleRadius + lineGap,
                centerY,
                centers[index + 1] - circleRadius - lineGap,
                centerY,
                linePaint
            )
        }
    }

    private fun drawStep(canvas: Canvas, centerX: Float, centerY: Float, index: Int) {
        val reached = index + 1 <= currentStep
        circleFillPaint.color = if (reached) activeColor else inactiveCircleColor
        canvas.drawCircle(centerX, centerY, circleRadius, circleFillPaint)

        if (reached) {
            canvas.drawCircle(centerX, centerY, innerRingRadius, ringPaint)
        }

        numberPaint.color = if (reached) activeTextColor else inactiveNumberColor
        val baseline = centerY - (numberPaint.descent() + numberPaint.ascent()) / 2
        canvas.drawText((index + 1).toString(), centerX, baseline, numberPaint)
    }

    private fun drawTitle(canvas: Canvas, centerX: Float, centerY: Float, index: Int) {
        titlePaint.color = if (index + 1 <= currentStep) activeTitleColor else inactiveTitleColor
        val baseline = centerY + circleRadius + titleTopSpacing - titlePaint.ascent()
        canvas.drawText(stepTitles.getOrElse(index) { "" }, centerX, baseline, titlePaint)
    }

    private fun dp(value: Float): Float {
        return value * resources.displayMetrics.density
    }

    private fun sp(value: Float): Float {
        return value * resources.displayMetrics.scaledDensity
    }

    companion object {
        private const val STEP_COUNT = 3
    }
}
