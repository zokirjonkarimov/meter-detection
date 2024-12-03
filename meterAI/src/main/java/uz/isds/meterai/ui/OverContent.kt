package uz.isds.meterai.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import uz.isds.meterai.other.BoundingBox

@Composable
fun BoundingBoxView(boundingBox: BoundingBox) {
    val borderWidth = 2.dp
    val cornerRadius = 16.dp
    val cnf = boundingBox.cnf
    val cnfText = "%.2f".format(cnf) // Format the cnf value to two decimal places

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // To'liq ekran foni
            drawRect(
                color = Color(0xFF151515).copy(alpha = 0.7f),
                size = size
            )

            // Bounding box koordinatalari
            val box = Rect(
                left = boundingBox.x1 * size.width,
                top = boundingBox.y1 * size.height,
                right = boundingBox.x2 * size.width,
                bottom = boundingBox.y2 * size.height
            )

            // Ichki qismni shaffof qilish
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(box.left, box.top),
                size = Size(box.width, box.height),
                blendMode = BlendMode.Clear, // Shaffof qilish
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
            )

            // Border chizish
            drawRoundRect(
                color = Color.Green,
                topLeft = Offset(box.left, box.top),
                size = Size(box.width, box.height),
                style = Stroke(width = borderWidth.toPx()),
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
            )
        }
        val cnfPositionX =
            boundingBox.cx * (LocalDensity.current.density * LocalConfiguration.current.screenWidthDp)
        val cnfPositionY = (boundingBox.cy * LocalDensity.current.density * LocalConfiguration.current.screenHeightDp)
        val offsetX = with(LocalDensity.current) { (cnfPositionX).toDp() }
        val offsetY = with(LocalDensity.current) { (cnfPositionY).toDp() }

        Text(
            text = cnfText,
            modifier = Modifier
                .align(Alignment.TopStart) // Align text at the top left of bounding box
                .offset(
                    x = offsetX,
                    y = offsetY
                )
                .background(Color.Green),
            color = Color.White,
        )
    }
}


