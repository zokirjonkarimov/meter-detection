package uz.isds.meterdetection

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.isds.meterai.AIActivity
import uz.isds.meterai.ui.component.TextApp
import uz.isds.meterai.ui.theme.whiteColor
import uz.isds.meterdetection.ui.theme.MeterDetectionTheme
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeterDetectionTheme {
                var resultText by remember { mutableStateOf<String?>(null) }
                var startSdk by remember { mutableStateOf(false) }
                var startSdkFromFile by remember { mutableStateOf(false) }
                var message by remember { mutableStateOf<String?>(null) }
                var code by remember { mutableIntStateOf(0) }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { startSdk = true }) { Text(text = "Start SDK") }
                        Button(onClick = { startSdkFromFile = true }) { Text(text = "From file") }
                        resultText?.let {
                            TextApp(it, modifier = Modifier.padding(bottom = 10.dp))
                        }
                        message?.let {
                            TextApp(it, modifier = Modifier.padding(bottom = 10.dp), color = Color.Red)
                        }
                        if (code != 0) {
                            TextApp("code:$code", modifier = Modifier.padding(bottom = 10.dp), color = Color.Red)
                        }
                    }
                }
                val activityResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == RESULT_OK) {
                        resultText = result.data?.getStringExtra("result")
                    }else if (result.resultCode == RESULT_CANCELED){
                        message = result.data?.getStringExtra("message")
                        code = result.data?.getIntExtra("code",0) ?: 0
                    }
                }

                LaunchedEffect(startSdk) {
                    if (startSdk) {
                        val intent = Intent(this@MainActivity, AIActivity::class.java)
                        intent.putExtra("x-api-key", "TqfrxrvI0rkLed7BF3fmJYr0yWmLHuTVxJAr5tWxuWIJ80hmM2PsFYPspRaAPPDZ")
                        activityResultLauncher.launch(intent)
                        startSdk = false
                    }
                }

                LaunchedEffect(startSdkFromFile) {
                    if (startSdkFromFile) {
                        val intent = Intent(this@MainActivity, AIActivity::class.java)
                        intent.putExtra("x-api-key", "TqfrxrvI0rkLed7BF3fmJYr0yWmLHuTVxJAr5tWxuWIJ80hmM2PsFYPspRaAPPDZ")
                        intent.putExtra("fromFile", startSdkFromFile)
                        activityResultLauncher.launch(intent)
                        startSdkFromFile = false
                    }
                }
            }
        }
    }
}

