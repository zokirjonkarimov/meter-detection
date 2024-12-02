package uz.isds.meterai.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import uz.isds.meterai.R
import uz.isds.meterai.other.BoundingBox
import uz.isds.meterai.other.Constants.MODEL_PATH
import uz.isds.meterai.other.Detector
import java.util.concurrent.Executors


@Composable
fun CameraScreen() {
    CameraPermissionWrapper {
        val boundingBox = remember { mutableStateOf<BoundingBox?>(null) }
        Box(modifier = Modifier.fillMaxSize()) {
            CameraContent(
                modelPath = MODEL_PATH,
                onBoundingBoxesDetected = { boxes, inferenceTime ->
                    Log.d("BoundingBoxes", "Detected: $boxes in $inferenceTime ms")
                    boundingBox.value = boxes
                },
                onEmptyDetection = {
                    Log.d("BoundingBoxes", "No detection")
                    boundingBox.value = null
                }
            )

            IconButton(onClick = { }, modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            boundingBox.value?.let {
//                Box(modifier = Modifier
//                    .fillMaxSize()
//                    .background(color = Color(0xFF151515).copy(0.7f)))
                BoundingBoxView(it)
            }
        }
    }
}


@Composable
private fun CameraContent(
    onBoundingBoxesDetected: (BoundingBox, Long) -> Unit = { _, _ -> },
    onEmptyDetection: () -> Unit = {},
    modelPath: String,
    isFrontCamera: Boolean = false,
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val coroutineScope = rememberCoroutineScope()
    var detector by remember { mutableStateOf<Detector?>(null) }
    LaunchedEffect(Unit) {
        cameraExecutor.execute {
            detector = Detector(context, modelPath, object : Detector.DetectorListener {
                override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
                    coroutineScope.launch {
                        onBoundingBoxesDetected(boundingBoxes.maxBy { it.cnf }, inferenceTime)
                    }
                }

                override fun onEmptyDetect() {
                    coroutineScope.launch {
                        onEmptyDetection()
                    }
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            detector = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        var camera = remember<Camera?> { null }
        var isTorchOn by remember { mutableStateOf(false) }

        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
//                    scaleType = PreviewView.ScaleType.FIT_START
                }
            },
            modifier = Modifier.fillMaxWidth(),
            update = { preview ->
                val cameraProvider = cameraProviderFuture.get()
//            val rotation = preview.display.rotation

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(if (isFrontCamera) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
                    .build()

                val previewUseCase = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//                .setTargetRotation(rotation)
                    .build()

                val imageAnalysisUseCase = ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                .setTargetRotation(rotation)
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
                                if (isFrontCamera) {
                                    postScale(
                                        -1f,
                                        1f,
                                        imageProxy.width / 2f,
                                        imageProxy.height / 2f
                                    )
                                }
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
            }
        )
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
                .background(Color.Red, CircleShape)
                .size(60.dp)
                .clickable {

                }, contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_camera),
                tint = Color.White,
                contentDescription = null
            )
        }

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


@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun CameraContentPreview() {
    CameraContent({ _, _ -> }, onEmptyDetection = {}, "", true)
}