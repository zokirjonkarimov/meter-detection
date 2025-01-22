package uz.isds.meterai.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Rect as ComposeRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import uz.isds.meterai.R
import uz.isds.meterai.other.BoundingBox

//@Composable
//fun BoundingBoxView(boundingBox: BoundingBox,bitmap: Bitmap) {
//    val borderWidth = 2.dp
//    val cornerRadius = 16.dp
//    val cnf = boundingBox.cnf
////    val cnfText = "%.2f".format(cnf) // Format the cnf value to two decimal places
//    val text = (boundingBox.cnf * 100).toInt().toString() + " %"
//    val textMeasurer = rememberTextMeasurer()
//    var offset by remember { mutableStateOf(Offset.Zero) }
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            // Bounding box koordinatalari
//            val box = Rect(
//                left = boundingBox.x1 * size.width,
//                top = boundingBox.y1 * size.height,
//                right = boundingBox.x2 * size.width,
//                bottom = boundingBox.y2 * size.height
//            )
//
//            offset = box.bottomCenter
////            if (box.left < 0 && box.top < 0 && box.right < size.width && box.bottom < size.width) {
//
//            // To'liq ekran foni
//            drawRect(
//                color = Color(0xFF151515).copy(alpha = 0.7f),
//                size = size
//            )
//
//
//            // Ichki qismni shaffof qilish
//            drawRoundRect(
//                color = Color.Transparent,
//                topLeft = Offset(box.left * 0.9f, box.top),
//                size = Size(boundingBox.w * size.width * 1.2f, boundingBox.h * size.height),
//                blendMode = BlendMode.Clear, // Shaffof qilish
//                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
//            )
//
//            // Border chizish
//            drawRoundRect(
//                color = Color.Green,
//                topLeft = Offset(box.left * 0.9f, box.top),
//                size = Size(boundingBox.w * size.width * 1.2f, boundingBox.h * size.height),
//                style = Stroke(width = borderWidth.toPx()),
//                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
//            )
//        }
//        val textSize = textMeasurer.measure(text)
//        val offsetY = with(LocalDensity.current) { (offset.y-textSize.size.width/4).toDp() }
//        val offsetX = with(LocalDensity.current) { (offset.x-textSize.size.width/8).toDp() }
//        TextApp(
//            text = text,
//            modifier = Modifier
//                .offset(
//                    x = offsetX,
//                    y = offsetY
//                )
//                .background(Color.Green),
//            color = Color.White,
//        )
//    }
//}



@Composable
fun BoundingBoxView(
    boundingBox: BoundingBox,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.border)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        boundingBox.let { box ->
            val left = box.x1 * width
            val top = box.y1 * height
            val right = box.x2 * width
            val bottom = box.y2 * height

            // Draw full screen background with transparency
            drawRect(
                color = Color(0xFF151515).copy(alpha = 0.7f),
                size = size
            )

            // Make the inner area transparent
            drawRoundRect(
                color = Color.Transparent,
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                cornerRadius =  CornerRadius(25.dp.toPx(context), 25.dp.toPx(context)),
                blendMode = BlendMode.Clear
            )

            // Draw the bitmap within the bounding box
            val rect = ComposeRect(left, top, right, bottom)
            drawIntoCanvas { canvas ->
                val androidRect = android.graphics.Rect(
                    rect.left.toInt(),
                    rect.top.toInt(),
                    rect.right.toInt(),
                    rect.bottom.toInt()
                )
                canvas.nativeCanvas.drawBitmap(
                    bitmap,
                    null,
                    androidRect,
                    null
                )
            }

            // Draw the confidence percentage background rectangle
            val confidenceText = "${(box.cnf * 100).toInt()}%"
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 30f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val bounds = android.graphics.Rect()
            textPaint.getTextBounds(confidenceText, 0, confidenceText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()

            val rectLeft = (left + right) / 2 - textWidth / 2f - 10
            val rectRight = (left + right) / 2 + textWidth / 2f + 10
            val rectBottom = bottom + textHeight + 20
            val rectTop = bottom + 10

            drawRoundRect(
                color = Color(0xFF3CB95D),
                topLeft = androidx.compose.ui.geometry.Offset(rectLeft, rectTop),
                size = androidx.compose.ui.geometry.Size(rectRight - rectLeft, rectBottom - rectTop),
                cornerRadius = CornerRadius(8.dp.toPx(context), 8.dp.toPx(context))
            )

            // Draw confidence text
            drawIntoCanvas { canvas ->
                val textX = (left + right) / 2f
                val textY = bottom + textHeight + 10
                canvas.nativeCanvas.drawText(confidenceText, textX, textY, textPaint)
            }
        }
    }
}

private fun Dp.toPx(context: android.content.Context): Float {
    return android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP,
        this.value,
        context.resources.displayMetrics
    )
}


