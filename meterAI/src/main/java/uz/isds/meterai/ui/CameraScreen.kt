package uz.isds.meterai.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import uz.isds.meterai.R
import uz.isds.meterai.backup.CameraFragment
import uz.isds.meterai.ui.intent.CameraIntent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.CameraUiState


@Composable
fun CameraScreen(presenter: CommonPresenter<CameraIntent, CameraUiState>) {
    CameraContent(presenter::onEventDispatcher)
}


@Composable
private fun CameraContent(intent: (CameraIntent) -> Unit) {
    val context = LocalContext.current
    val fragmentManager = (context as AppCompatActivity).supportFragmentManager

    val fragment = remember { CameraFragment() }

    AndroidView(factory = {
        val fragmentContainer = FragmentContainerView(it).apply {
            id = R.id.container
        }
        fragmentManager.beginTransaction()
            .replace(R.id.container,fragment)
            .commit()

        fragmentContainer
    }, modifier = Modifier.fillMaxSize())
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun CameraContentPreview() {
    CameraContent({})
}