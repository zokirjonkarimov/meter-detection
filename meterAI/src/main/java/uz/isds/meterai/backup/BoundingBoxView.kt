package uz.isds.meterai.backup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BoundingBoxView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var results = listOf<BoundingBox>()
    fun clear() {
        results = listOf()
        invalidate()
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 36f
        textAlign = Paint.Align.CENTER
    }

    private val confidencePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }

    private val progressRect = RectF() // RectF-ni oldindan yaratish
    private val boundingRect = RectF() // Bounding box uchun

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        results.forEach { box ->
            boundingRect.set(box.x1, box.y1, box.x2, box.y2)
            canvas.drawRect(boundingRect, borderPaint)

            // Aniqlik foizi bloki uchun RectF-ni qayta ishlatish
            progressRect.set(
                box.cx - 60f, // Aniqlik bloki kengligi
                box.y2 + 20f,
                box.cx + 60f,
                box.y2 + 70f
            )
            canvas.drawRoundRect(progressRect, 20f, 20f, confidencePaint)

            // Aniqlik foizini yozish
            canvas.drawText("${(box.cnf * 100).toInt()}%", box.cx, box.y2 + 55f, textPaint)
        }
        // To'rtburchak chegarasini oldindan hisoblash

    }

    fun setResults(boundingBoxes: List<BoundingBox>) {
        results = boundingBoxes
        invalidate()
    }
}

