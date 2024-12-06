package uz.isds.meterai.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.isds.meterai.other.BoundingBox
import uz.isds.meterai.ui.component.TextApp

@Composable
fun BoundingBoxView(boundingBox: BoundingBox) {
    val borderWidth = 2.dp
    val cornerRadius = 16.dp
    val cnf = boundingBox.cnf
//    val cnfText = "%.2f".format(cnf) // Format the cnf value to two decimal places
    val text = (boundingBox.cnf * 100).toInt().toString() + " %"
    val textMeasurer = rememberTextMeasurer()
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            Log.d("TTTT", "canvas: ${size.width} ${size.height}")
            // Bounding box koordinatalari
            val box = Rect(
                left = boundingBox.x1 * size.width,
                top = boundingBox.y1 * size.height,
                right = boundingBox.x2 * size.width,
                bottom = boundingBox.y2 * size.height
            )

            offset = box.bottomCenter
//            if (box.left < 0 && box.top < 0 && box.right < size.width && box.bottom < size.width) {

            // To'liq ekran foni
            drawRect(
                color = Color(0xFF151515).copy(alpha = 0.7f),
                size = size
            )


            // Ichki qismni shaffof qilish
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(box.left * 0.9f, box.top),
                size = Size(boundingBox.w * size.width * 1.2f, boundingBox.h * size.height),
                blendMode = BlendMode.Clear, // Shaffof qilish
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
            )

            // Border chizish
            drawRoundRect(
                color = Color.Green,
                topLeft = Offset(box.left * 0.9f, box.top),
                size = Size(boundingBox.w * size.width * 1.2f, boundingBox.h * size.height),
                style = Stroke(width = borderWidth.toPx()),
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
            )
//
//            val textSize = textMeasurer.measure(text = text)
//
//            // Define padding around the text
//            val padding = 8.dp.toPx()
//
//            // Calculate the background rectangle size and position
//            val rectWidth = (textSize.size.width + 2 * padding + 2.dp.toPx()) * 1.2f
//            val rectHeight = (textSize.size.height + 2 * padding) / 1.5f
//            val rectTopLeft = Offset(
//                (box.bottomCenter.x - rectWidth / 2),
//                (box.bottomCenter.y - 24)
//            )

            // Draw the rounded rectangle background
//            drawRoundRect(
//                color = Color.Green,
//                topLeft = rectTopLeft,
//                size = Size(rectWidth, rectHeight),
//                cornerRadius = CornerRadius(7.5.dp.toPx(), 7.5.dp.toPx()) // Rounded corners
//            )
//            // Draw the text over the background
//            drawText(
//                textMeasurer = textMeasurer,
//                text = text,
//                topLeft = Offset(
//                    (box.bottomCenter.x) + 32,
//                    (box.bottomCenter.y - 8)
//                ),
//                style = TextStyle(
//                    color = Color.White,
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            )
//            }
        }
        val textSize = textMeasurer.measure(text)
        val offsetY = with(LocalDensity.current) { (offset.y-textSize.size.width/4).toDp() }
        val offsetX = with(LocalDensity.current) { (offset.x-textSize.size.width/8).toDp() }
        TextApp(
            text = text,
            modifier = Modifier
                .offset(
                    x = offsetX,
                    y = offsetY
                )
                .background(Color.Green),
            color = Color.White,
        )
    }
}


