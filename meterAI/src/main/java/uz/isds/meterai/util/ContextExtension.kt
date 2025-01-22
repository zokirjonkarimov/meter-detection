package uz.isds.meterai.util

import android.content.Context
import android.content.pm.PackageManager

fun Context.isFlashSupported(): Boolean {
    val packageManager = packageManager
    return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
}