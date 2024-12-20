package uz.isds.meterai.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import uz.isds.meterai.MainActivity
import uz.isds.meterai.R
import uz.isds.meterai.data.response.ImageUploadResponse
import uz.isds.meterai.other.Base64Image
import uz.isds.meterai.ui.component.TextApp
import uz.isds.meterai.ui.intent.ResultIntent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.theme.backgroundColor
import uz.isds.meterai.ui.theme.primaryColor
import uz.isds.meterai.ui.theme.textColor
import uz.isds.meterai.ui.uistate.ResultUiState
import kotlin.random.Random

@Composable
fun ResultScreen(presenter: CommonPresenter<ResultIntent, ResultUiState>) {
    ResultContent(presenter.uiState.subscribeAsState().value, presenter::onEventDispatcher)
}

@Composable
private fun ResultContent(uiState: ResultUiState, intent: (ResultIntent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                IconButton(
                    onClick = { intent(ResultIntent.OpenCamera) },
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                ) {
                    Icon(painter = painterResource(R.drawable.ic_back), contentDescription = null)
                }

                TextApp(text = "Результат", fontWeight = FontWeight(700), fontSize = 22.sp)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.info_circle),
                    tint = Color(0xFF47404F),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp)
                )
                TextApp(
                    "Проверьте данные. Если они верны, подтвердите. В случае ошибки переснимите или отредактируйте",
                    lineHeight = 19.5.sp
                )
            }

            TextApp(
                text = "Фрагмент для обработки",
                Modifier.padding(start = 16.dp, bottom = 6.dp),
                color = Color(0xFF7F7A84),
                fontSize = 12.sp
            )
            // imagega almashtir
            Base64Image(
                base64String = uiState.data.croppedImage ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF3F3F3))
            )

            TextApp(
                fontSize = 12.sp,
                text = "Результат распознавания",
                modifier = Modifier.padding(start = 16.dp, bottom = 6.dp, top = 20.dp),
                color = Color(0xFF7F7A84)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .defaultMinSize(minHeight = 50.dp)
                    .background(Color(0xFFF3F3F3), RoundedCornerShape(10.dp))
                    .padding(vertical = 7.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if ((uiState.data.data?.result?.size ?: 0) > 4) {
                    repeat(5) {
                        val result = uiState.data.data?.result?.get(it)
                        val percentage = uiState.data.data?.percent?.get(it) ?: 0.0
                        Box(
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .defaultMinSize(minWidth = 26.dp, minHeight = 36.dp)
                                .border(
                                    1.dp, when {
                                        percentage > 0.98 -> Color(0xFF3CB95D)
                                        percentage > 0.96 -> Color(0xFFFFC400)
                                        percentage > 0.0 -> Color(0xFFED1C24)
                                        else -> Color.Transparent
                                    }, RoundedCornerShape(7.25.dp)
                                )
                                .background(
                                    backgroundColor,
                                    RoundedCornerShape(7.25.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            TextApp(result ?: "", fontWeight = FontWeight(700), fontSize = 13.sp)
                        }
                    }
                }
                if ((uiState.data.data?.result?.size ?: 0) >= 8) {
                    TextApp(
                        ",", modifier = Modifier
                            .align(Alignment.Bottom)
                            .padding(end = 2.dp)
                    )
                    repeat(3) {
                        val result = uiState.data.data?.result?.get(it + 5)
                        val percentage = uiState.data.data?.percent?.get(it + 5) ?: 0.0
                        Box(
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .defaultMinSize(minWidth = 26.dp, minHeight = 36.dp)
                                .border(
                                    1.dp, when {
                                        percentage > 0.98 -> Color(0xFF3CB95D)
                                        percentage > 0.96 -> Color(0xFFFFC400)
                                        percentage > 0.0 -> Color(0xFFED1C24)
                                        else -> Color.Transparent
                                    }, RoundedCornerShape(7.25.dp)
                                )
                                .background(
                                    backgroundColor,
                                    RoundedCornerShape(7.25.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            TextApp(
                                result ?: "",
                                fontWeight = FontWeight(700),
                                fontSize = 13.sp,
                                color = primaryColor
                            )
                        }
                    }
                    TextApp(
                        text = "м3", modifier = Modifier
                            .align(Alignment.Bottom), fontSize = 13.sp
                    )
                }
            }

            TextApp(
                fontSize = 12.sp,
                text = "Точность распозования:",
                modifier = Modifier.padding(start = 16.dp, bottom = 10.dp, top = 40.dp),
                color = Color(0xFF7F7A84)
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(0.33f)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(6.dp)
                            .background(Color(0xFF3CB95D), CircleShape)
                    )
                    TextApp(
                        fontSize = 12.sp,
                        text = "Высокий",
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(0.33f)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(6.dp)
                            .background(Color(0xFFFFC400), CircleShape)
                    )
                    TextApp(
                        fontSize = 12.sp,
                        text = "Средний",
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(0.33f)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(6.dp)
                            .background(Color(0xFFED1C24), CircleShape)
                    )
                    TextApp(
                        fontSize = 12.sp,
                        text = "Низкий",
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(62.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE4E3E5))
                        .clickable { intent(ResultIntent.OpenCamera) }, contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_refresh),
                        contentDescription = null,
                        tint = textColor
                    )
                }

                TextApp(text = "Переснять")
            }


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(primaryColor)
                        .clickable {
                            val returnIntent = Intent().apply {
                                val stringBuild = StringBuilder()
                                uiState.data.data?.result?.filterNotNull()?.forEach {
                                    stringBuild.append(it)
                                }
                                putExtra("result", stringBuild.toString())
                            }
                            (context as MainActivity).apply {
                                setResult(Activity.RESULT_OK, returnIntent)
                                finish()
                            }
                        }, contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_checked),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                TextApp(text = "Подтвердить")
            }
        }
    }

    BackHandler {
        intent(ResultIntent.OpenCamera)
    }
}

@Preview
@Composable
private fun ResultPreview() {
    ResultContent(ResultUiState(ImageUploadResponse())) {}
}
