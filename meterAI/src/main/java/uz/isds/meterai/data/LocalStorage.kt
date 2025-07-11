package uz.isds.meterai.data

import android.content.Context
import androidx.core.content.edit

class LocalStorage(context: Context) {
    private val preference = context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)

    var fileUrl: String?
        get() = preference.getString("fileUrl", null)
        set(value) {
            preference.edit { putString("fileUrl", value) }
        }
}