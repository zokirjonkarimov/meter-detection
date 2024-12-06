package uz.isds.meterai.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import uz.isds.meterai.R
import uz.isds.meterai.ui.component.TextApp
import uz.isds.meterai.ui.intent.ImageConfirmIntent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.theme.backgroundColor
import uz.isds.meterai.ui.theme.primaryColor
import uz.isds.meterai.ui.theme.textColor
import uz.isds.meterai.ui.uistate.ImageConfirmUiState

@Composable
fun ImageConfirmScreen(presenter: CommonPresenter<ImageConfirmIntent, ImageConfirmUiState>) {
    ImageConfirmContent(presenter.uiState.subscribeAsState().value, presenter::onEventDispatcher)
}

@Composable
fun ImageConfirmContent(uiState: ImageConfirmUiState, intent: (ImageConfirmIntent) -> Unit) {
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
                    onClick = { intent(ImageConfirmIntent.Back) },
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                ) {
                    Icon(painter = painterResource(R.drawable.ic_back), contentDescription = null)
                }

                TextApp(text = "Подтверждение", fontWeight = FontWeight(700), fontSize = 22.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.padding(end = 10.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.info_circle),
                        tint = Color(0xFF47404F),
                        contentDescription = null
                    )
                }
                TextApp(
                    "Проверьте качество сделанного фото. Если изображение нечеткое, то переснимите его",
                    lineHeight = 19.5.sp
                )
            }
            uiState.bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(horizontal = 16.dp).clip(
                        RoundedCornerShape(16.dp)
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
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
                        .clickable { intent(ImageConfirmIntent.Back) }, contentAlignment = Alignment.Center
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
                Box(
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(primaryColor)
                        .clickable { intent(ImageConfirmIntent.Done) }, contentAlignment = Alignment.Center
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
}

@Preview
@Composable
private fun ImageConfirm() {
//    ImageConfirmScreen(instance.presenter)
}