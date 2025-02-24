package uz.isds.meterai.ui.intent

import android.graphics.Bitmap
import uz.isds.meterai.other.Detector

sealed interface FileUploadIntent {
    class DetectBitmap(val detector: Detector,val bitmap: Bitmap?) : FileUploadIntent
    data object OpenCamera : FileUploadIntent
    data object ToastHide : FileUploadIntent
    data object Dismiss : FileUploadIntent
}
