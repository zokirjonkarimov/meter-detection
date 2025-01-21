package uz.isds.meterai.backup

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import uz.isds.meterai.backup.Constants.MODEL_PATH
import uz.isds.meterai.R
import uz.isds.meterai.databinding.CameraLayoutBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(), Detector.DetectorListener {
    private var binding: CameraLayoutBinding? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var detector: Detector? = null
    private lateinit var cameraExecutor: ExecutorService
    private var result : Result? = null
    private var listener : ((Bitmap) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CameraLayoutBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraExecutor.execute {
            detector = Detector(requireContext(), MODEL_PATH)
            detector?.onDetect(this)
        }
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        bindListeners()
    }

    private fun bindListeners() {
        binding?.apply {
            take.setOnClickListener {
                result?.let {
                    listener?.invoke(cropBitmap(it.bitmap,it.boundingBox))
                }
            }

            iconButton.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider  = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

//        val rotation = binding.viewFinder.display.rotation

        val cameraSelector = CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview =  Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .setTargetRotation(binding.viewFinder.display.rotation)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
            val bitmapBuffer =
                Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )
            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
            imageProxy.close()

            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
                matrix, true
            )

            detector?.detect(rotatedBitmap)
        }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                requireActivity(),
                cameraSelector,
                preview,
                imageAnalyzer
            )

            preview?.setSurfaceProvider(binding?.viewFinder?.surfaceProvider)
        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.CAMERA] == true) { startCamera() }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector?.close()
//        detector = null
        binding = null
        cameraExecutor.shutdown()
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()){
            startCamera()
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    companion object {
        private const val TAG = "Camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf (
            Manifest.permission.CAMERA
        ).toTypedArray()
    }

    override fun onEmptyDetect() {
        requireActivity().runOnUiThread {
            binding?.overlay?.clear()
            binding?.take?.setImageResource(R.drawable.bt_disable)
            result = null
        }
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, bitmap: Bitmap) {
        requireActivity().runOnUiThread {
//            binding.inferenceTime.text = "${inferenceTime}ms"
            binding?.take?.setImageResource(R.drawable.bt_unable)
            binding?.overlay?.let {
                val max = boundingBoxes.maxBy { it.cnf }
                result = Result(max,bitmap)
                it.setResults(max)
                it.invalidate()
            }
        }
    }

    private fun cropBitmap(bitmap: Bitmap, boundingBox: BoundingBox, cornerRadiusPx: Float = 7.5f): Bitmap {
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

    fun onCuptureClick(listener : (Bitmap) -> Unit){
        this.listener = listener
    }
}

data class Result(val boundingBox: BoundingBox,val bitmap: Bitmap)