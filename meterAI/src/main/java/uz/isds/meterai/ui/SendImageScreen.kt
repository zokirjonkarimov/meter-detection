package uz.isds.meterai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.isds.meterai.R
import uz.isds.meterai.ui.component.TextApp
import uz.isds.meterai.ui.intent.SendImageIntent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.theme.backgroundColor
import uz.isds.meterai.ui.theme.primaryColor
import uz.isds.meterai.ui.theme.textColor
import uz.isds.meterai.ui.uistate.SendImageUiState

@Composable
fun SendImageScreen(presenter: CommonPresenter<SendImageIntent, SendImageUiState>) {
    SendImageContent()
}

@Composable
private fun SendImageContent() {
    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        IconButton(onClick = {}, modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                tint = textColor,
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = primaryColor)
            TextApp(
                text = "Подождите, определяем данные с фото",
                modifier = Modifier.padding(top = 20.dp),
                fontSize = 13.sp
            )
        }
    }
}

@Preview
@Composable
private fun SendImagePreview() {
    SendImageContent()
}