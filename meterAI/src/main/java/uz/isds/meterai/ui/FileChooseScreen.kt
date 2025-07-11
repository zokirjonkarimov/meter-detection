package uz.isds.meterai.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import uz.isds.meterai.data.LocalStorage
import uz.isds.meterai.other.Constants.MODEL_PATH
import uz.isds.meterai.ui.component.TextApp
import uz.isds.meterai.ui.component.ToastError
import uz.isds.meterai.ui.intent.FileUploadIntent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.theme.backgroundColor
import uz.isds.meterai.ui.theme.whiteColor
import uz.isds.meterai.ui.uistate.FileUploadUiState
import uz.isds.meterai.other.Detector
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

var uri: Uri? = null

@Composable
fun FileChooseScreen(presenter: CommonPresenter<FileUploadIntent, FileUploadUiState>) {
    FileChooseContent(presenter.uiState.subscribeAsState().value, presenter::onEventDispatcher)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileChooseContent(uiState: FileUploadUiState, intent: (FileUploadIntent) -> Unit) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        sheetSwipeEnabled = false,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding(),
        scaffoldState = scaffoldState,
        sheetContent = {
            val context = LocalContext.current
            val galleryLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { it ->
                it?.let {
                    intent(
                        FileUploadIntent.DetectBitmap(
                            Detector(context, uri!!),
                            uriToBitmap(context, it)
                        )
                    )
                }
            }

            TextApp(
                text = "Выбрать вариант",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight(700)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { intent(FileUploadIntent.OpenCamera) },
                    enabled = uri != null
                ) {
                    Text("Камера")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    enabled = uri != null
                ) {
                    Text("Галерея")
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            if (uiState.loading)
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            if (uiState.errorChooseImage) {
                ToastError(text = "Счетчик не обнаружен во встроенном изображении. Повторите попытку.") {
                    intent(FileUploadIntent.ToastHide)
                }
            }
        }

        LaunchedEffect(Unit) {
            scaffoldState.bottomSheetState.expand()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseImageDialog(
    onDismiss: () -> Unit,
    onResult: (Bitmap?) -> Unit,
    onCamera: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss, windowInsets = BottomSheetDefaults.windowInsets.only(
            WindowInsetsSides.Bottom
        ), containerColor = whiteColor
    ) {

    }
}

private fun uriToBitmap(context: Context, uri: Uri?): Bitmap? {
    if (uri == null) return null
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun copyModelFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val tempFile = File.createTempFile("model_temp", ".tflite", context.cacheDir)
    FileOutputStream(tempFile).use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return tempFile
}