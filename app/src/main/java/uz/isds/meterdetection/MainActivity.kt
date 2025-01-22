package uz.isds.meterdetection

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.isds.meterai.AIActivity
import uz.isds.meterai.ui.component.TextApp
import uz.isds.meterdetection.ui.theme.MeterDetectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeterDetectionTheme {
                var resultText by remember { mutableStateOf<String?>(null) }
                var startSdk by remember { mutableStateOf(false) }
                var message by remember { mutableStateOf<String?>(null) }
                var code by remember { mutableIntStateOf(0) }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { startSdk = true }) { Text(text = "Start SDK") }
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
            }
        }
    }
}