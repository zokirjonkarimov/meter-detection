package uz.isds.meterai.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import uz.isds.meterai.AIActivity
import uz.isds.meterai.R
import uz.isds.meterai.other.BoundingBox
import uz.isds.meterai.other.Detector
import uz.isds.meterai.util.isFlashSupported
import uz.isds.meterai.other.Constants.MODEL_PATH
import uz.isds.meterai.other.Result
import uz.isds.meterai.ui.intent.CameraIntent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.theme.primaryColor
import uz.isds.meterai.ui.uistate.CameraUiState
import java.util.concurrent.Executors


@Composable
fun CameraScreen(presenter: CommonPresenter<CameraIntent, CameraUiState>) {
    CameraPermissionWrapper {
        CameraContent(presenter::onEventDispatcher)
    }
}


//@Composable
//private fun CameraContent(intent: (CameraIntent) -> Unit) {
//    val context = LocalContext.current
//    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
//
//    val fragment = remember { CameraFragment() }
//    AndroidView(factory = {
//        val fragmentContainer = FragmentContainerView(it).apply {
//            id = R.id.container
//        }
//        fragmentManager.beginTransaction()
//            .replace(R.id.container, fragment)
//            .commit()
//        fragment.onCuptureClick { intent(CameraIntent.OnCupture(it)) }
//        fragmentContainer
//    }, modifier = Modifier.fillMaxSize())
//}
@Composable
private fun CameraContent(intent: (CameraIntent) -> Unit) {
    val boundingBox = remember { mutableStateOf<Result?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val isFlashSupport = remember { context.isFlashSupported() }
    val coroutineScope = rememberCoroutineScope()
    var detector by remember { mutableStateOf<Detector?>(null) }
    val listener = remember {
        object : Detector.DetectorListener {
            override fun onDetect(boundingBoxes: List<BoundingBox>, bitmap: Bitmap) {
                coroutineScope.launch {
                    boundingBox.value = Result(boundingBoxes.maxBy { it.cnf }, bitmap)
                }
            }

            override fun onEmptyDetect() {
                coroutineScope.launch {
                    boundingBox.value = null
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        cameraExecutor.execute {
            detector = Detector(context, MODEL_PATH)
            detector?.onDetect(listener)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            detector = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        var camera = remember<Camera?> { null }
        var isTorchOn by remember { mutableStateOf(false) }

        AndroidView(
            factory = { ctx ->
                val preview = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val rotation: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    context.display.rotation
                } else {
                    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    wm.defaultDisplay.rotation
                }
                val cameraProvider = cameraProviderFuture.get()
//            val rotation = preview.display.rotation

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                val previewUseCase = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setTargetRotation(rotation)
                    .build()

                val imageAnalysisUseCase = ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setTargetRotation(rotation)
                    .build().apply {
                        setAnalyzer(cameraExecutor) { imageProxy ->
                            val bitmapBuffer = Bitmap.createBitmap(
                                imageProxy.width,
                                imageProxy.height,
                                Bitmap.Config.ARGB_8888
                            )
                            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }

                            val matrix = Matrix().apply {
                                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                            }

                            val rotatedBitmap = Bitmap.createBitmap(
                                bitmapBuffer,
                                0,
                                0,
                                bitmapBuffer.width,
                                bitmapBuffer.height,
                                matrix,
                                true
                            )

                            detector?.detect(rotatedBitmap)
                        }
                    }

                cameraProvider.unbindAll()

                try {
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        previewUseCase,
                        imageAnalysisUseCase
                    )

                    previewUseCase.setSurfaceProvider(preview.surfaceProvider)
                } catch (e: Exception) {
                    Log.e("CameraScreen", "Failed to bind use cases", e)
                }
                preview
            },
            modifier = Modifier.fillMaxWidth()
        )
        boundingBox.value?.let {
            BoundingBoxView(it.boundingBox)
        }
        IconButton(
            onClick = { (context as AIActivity).finish() },
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                tint = Color.White
            )
        }
        if (isFlashSupport)
            IconButton(
                onClick = { camera?.cameraControl?.enableTorch(!isTorchOn) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_flash),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(if (boundingBox.value == null) 0.5f else 1f))
                .size(60.dp)
                .clickable(boundingBox.value != null) {
                    intent(
                        CameraIntent.OnCupture(
                            cropBitmap(
                                boundingBox.value!!.bitmap,
                                boundingBox.value!!.boundingBox
                            )
                        )
                    )
                }, contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_camera),
                tint = Color.White,
                contentDescription = null
            )
        }

        if (isFlashSupport)
            LaunchedEffect(Unit) {
                camera?.cameraInfo?.torchState?.observe(lifecycleOwner) { torchState ->
                    isTorchOn = torchState == TorchState.ON
                }
            }
    }
}


@Composable
fun CameraPermissionWrapper(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val permissionGranted = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionGranted.value = result.all { it.value }
        if (!permissionGranted.value) {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }) {
            permissionGranted.value = true
        } else {
            launcher.launch(permissions)
        }
    }

    if (permissionGranted.value) {
        content()
    }
}

fun cropBitmap(bitmap: Bitmap, boundingBox: BoundingBox, cornerRadiusPx: Float = 7.5f): Bitmap {
    // Normalize qilish va Boxni piksellarda aniqlash
    val box = android.graphics.Rect(
        (boundingBox.x1 * bitmap.width).toInt(),
        (boundingBox.y1 * bitmap.height).toInt(),
        (boundingBox.x2 * bitmap.width).toInt(),
        (boundingBox.y2 * bitmap.height).toInt()
    )

    // Yangi bitmap yaratish
    val croppedBitmap = Bitmap.createBitmap(
        box.width(), // Yangi bitmapning kengligi
        box.height(), // Yangi bitmapning balandligi
        Bitmap.Config.ARGB_8888 // Shaffoflikni saqlash uchun format
    )

    // Yangi bitmapga chizish
    val canvas = Canvas(croppedBitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Asl bitmapdan faqat box ichidagi qismini kesib olish
    canvas.drawBitmap(
        bitmap,
        android.graphics.Rect(box.left, box.top, box.right, box.bottom),
        android.graphics.Rect(0, 0, box.width(), box.height()),
        paint
    )

    // Agar burchak radiusi bo‘lsa, uni ishlatish
    val path = Path().apply {
        addRoundRect(
            0f,
            0f,
            box.width().toFloat(),
            box.height().toFloat(),
            cornerRadiusPx,
            cornerRadiusPx,
            Path.Direction.CW
        )
    }
    canvas.clipPath(path)

    return croppedBitmap
}


@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun CameraContentPreview() {
    CameraContent {}
}