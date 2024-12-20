package uz.isds.meterai.backup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import uz.isds.meterai.R

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results = listOf<BoundingBox>()
    private val boxPaint = Paint()
    private val textPaint = Paint()
    private val confidencePaint = Paint()
    private val cornerPaint = Paint()

    private val bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        results = listOf()
        invalidate()
    }

    private fun initPaints() {
        boxPaint.color = Color.GREEN
        boxPaint.strokeWidth = 6F
        boxPaint.style = Paint.Style.STROKE

        // Burchaklar uchun
        cornerPaint.color = Color.GREEN
        cornerPaint.strokeWidth = 10F
        cornerPaint.style = Paint.Style.STROKE

        confidencePaint.color = Color.parseColor("#3CB95D") // Fon rangi
        confidencePaint.style = Paint.Style.FILL

        textPaint.color = Color.WHITE // Matn rangi
        textPaint.textSize = 40f // Matn o‘lchami (12sp = taxminan 40f)
        textPaint.textAlign = Paint.Align.CENTER // Matnni o‘rtada joylash
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        results.forEach {
            val left = it.x1 * width
            val top = it.y1 * height
            val right = it.x2 * width
            val bottom = it.y2 * height

            // To'rtburchak burchaklar bilan chizish
            drawRoundedCorners(canvas, left, top, right, bottom)

            // Aniqlik matnini yozish (Kotlin)
            val confidenceText = "${(it.cnf * 100).toInt()}%"
            textPaint.getTextBounds(confidenceText, 0, confidenceText.length, bounds)

            // Aniqlik matni uchun joyni hisoblash
            val textWidth = bounds.width()
            val textHeight = bounds.height()

            // Aniqlik matni uchun fonni chizish
            val rectLeft = (left + right) / 2 - textWidth / 2f - 10
            val rectRight = (left + right) / 2 + textWidth / 2f + 10
            val rectBottom = bottom + textHeight + 20
            val rectTop = bottom + 10
            canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, confidencePaint)

            // Aniqlik matnini chizish
            val textX = (left + right) / 2f
            val textY = bottom + textHeight + 10
            canvas.drawText(confidenceText, textX, textY, textPaint)
        }
    }

    private fun drawRoundedCorners(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        val cornerLength = 50f

        // Yuqori chap
        canvas.drawLine(left, top, left + cornerLength, top, cornerPaint)
        canvas.drawLine(left, top, left, top + cornerLength, cornerPaint)

        // Yuqori o'ng
        canvas.drawLine(right, top, right - cornerLength, top, cornerPaint)
        canvas.drawLine(right, top, right, top + cornerLength, cornerPaint)

        // Pastki chap
        canvas.drawLine(left, bottom, left + cornerLength, bottom, cornerPaint)
        canvas.drawLine(left, bottom, left, bottom - cornerLength, cornerPaint)

        // Pastki o'ng
        canvas.drawLine(right, bottom, right - cornerLength, bottom, cornerPaint)
        canvas.drawLine(right, bottom, right, bottom - cornerLength, cornerPaint)
    }

    fun setResults(boundingBoxes: List<BoundingBox>) {
        results = boundingBoxes
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}

