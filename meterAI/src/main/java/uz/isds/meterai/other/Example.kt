package uz.isds.meterai.other



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ObjectDetectionCanvas(
    modifier: Modifier = Modifier,
    boundingBox: BoundingBox,
    cornerLength: Dp = 20.dp, // Burchak uzunligi
    strokeWidth: Dp = 4.dp, // Quti chizig'i qalinligi
    color: Color = Color.Green // Rangi
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val left = boundingBox.x1 * size.width
            val top = boundingBox.y1 * size.height
            val right = boundingBox.x2 * size.width
            val bottom = boundingBox.y2 * size.height
            val cornerPx = cornerLength.toPx()

            // Quti burchaklarini chizish
            val stroke = Stroke(width = strokeWidth.toPx(), pathEffect = PathEffect.cornerPathEffect(12f))

            // Chap yuqori
            drawLine(color, Offset(left, top), Offset(left + cornerPx, top), strokeWidth.toPx())
            drawLine(color, Offset(left, top), Offset(left, top + cornerPx), strokeWidth.toPx())

            // O'ng yuqori
            drawLine(color, Offset(right, top), Offset(right - cornerPx, top), strokeWidth.toPx())
            drawLine(color, Offset(right, top), Offset(right, top + cornerPx), strokeWidth.toPx())

            // Chap pastki
            drawLine(color, Offset(left, bottom), Offset(left + cornerPx, bottom), strokeWidth.toPx())
            drawLine(color, Offset(left, bottom), Offset(left, bottom - cornerPx), strokeWidth.toPx())

            // O'ng pastki
            drawLine(color, Offset(right, bottom), Offset(right - cornerPx, bottom), strokeWidth.toPx())
            drawLine(color, Offset(right, bottom), Offset(right, bottom - cornerPx), strokeWidth.toPx())
        }

        // Aniqlik darajasini pastki markazda ko'rsatish
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // Pastki markazda
                .padding(bottom = 16.dp)
        ) {
            Surface(
                color = Color.Green,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${(boundingBox.cnf * 100).toInt()}%", // 95% formatida
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun DetectionOverlayScreen() {
    val boundingBox = BoundingBox(
        x1 = 0.2f, y1 = 0.3f, x2 = 0.8f, y2 = 0.7f, cnf = 0.95f, clsName = "dad", cx = 0.0f, cy = 0.0f, w = 0f, h = 0f, cls = 0)

    Box(modifier = Modifier.fillMaxSize()) {
        // Aniqlangan quti ustida chizish
        ObjectDetectionCanvas(
            boundingBox = boundingBox,
            cornerLength = 32.dp, // Burchak uzunligi
            strokeWidth = 6.dp,   // Chiziq qalinligi
            color = Color.Green   // Quti rangi
        )

        // Ishonch qiymatini ko'rsatish
        Surface(
            color = Color.Green,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "${(boundingBox.cnf * 100).toInt()}%",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
