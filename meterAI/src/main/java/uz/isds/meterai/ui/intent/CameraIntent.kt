package uz.isds.meterai.ui.intent

import android.graphics.Bitmap

sealed interface CameraIntent {
    data object OnBack : CameraIntent
    class OnCupture(val bitmap: Bitmap?) : CameraIntent
}