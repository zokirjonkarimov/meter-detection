package uz.isds.meterdetection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.isds.meterai.MainActivity
import uz.isds.meterai.ui.component.TextApp
import uz.isds.meterdetection.ui.theme.MeterDetectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeterDetectionTheme {
                var resultText by remember { mutableStateOf("") }
                var startSdk by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { startSdk = true }) { Text(text = "Strat SDK") }
                        TextApp(resultText, modifier = Modifier.padding(bottom = 10.dp))
                    }
                }
                val activityResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == RESULT_OK) {
                        resultText = result.data?.getStringExtra("result") ?: ""
                    }
                }

                LaunchedEffect(startSdk) {
                    if (startSdk) {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra("x-api-key", "TqfrxrvI0rkLed7BF3fmJYr0yWmLHuTVxJAr5tWxuWIJ80hmM2PsFYPspRaAPPDZ")
                        activityResultLauncher.launch(intent)
                        startSdk = false
                    }
                }
            }
        }
    }
}