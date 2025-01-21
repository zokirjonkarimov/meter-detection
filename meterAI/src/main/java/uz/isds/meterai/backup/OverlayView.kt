package uz.isds.meterai.backup

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.isds.meterai.R

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: BoundingBox? = null
    private val textPaint = Paint()
    private val confidencePaint = Paint()
    private val paint = Paint()
    private lateinit var bitmap: Bitmap
    private val bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        results = null
        invalidate()
    }

    private fun initPaints() {
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.border)
        confidencePaint.color = Color.parseColor("#3CB95D") // Fon rangi
        confidencePaint.style = Paint.Style.FILL
        textPaint.color = Color.WHITE // Matn rangi
        textPaint.textSize = 30f // Matn o‘lchami (12sp = taxminan 40f)
        textPaint.textAlign = Paint.Align.CENTER // Matnni o‘rtada joylash
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let {
            val width = width.toFloat()
            val height = height.toFloat()

            val left = it.x1 * width
            val top = it.y1 * height
            val right = it.x2 * width
            val bottom = it.y2 * height

            // Draw full screen background with transparency
            val paintBackground = Paint().apply {
                color = Color.parseColor("#151515")
                alpha = (0.7 * 255).toInt()  // Alpha 0.7
            }
            canvas.drawRect(0f, 0f, width, height, paintBackground)

            // Make the inner area transparent (using clear blend mode equivalent)
            val paintClear = Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
            canvas.drawRoundRect(
                left, top,
                right, bottom,
                16.dp.toPx(context), 16.dp.toPx(context), paintClear
            )
            val box = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            canvas.drawBitmap(bitmap, null, box, paint)

//          Aniqlik matnini yozish
            val confidenceText = "${(it.cnf * 100).toInt()}%"
            textPaint.getTextBounds(confidenceText, 0, confidenceText.length, bounds)

            // Aniqlik matni uchun joyni hisoblash
            val textWidth = bounds.width()
            val textHeight = bounds.height()
//
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

    fun setResults(boundingBoxes: BoundingBox) {
        results = boundingBoxes
        invalidate()
    }

    private fun Dp.toPx(context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.value,
            context.resources.displayMetrics
        )
    }
}

