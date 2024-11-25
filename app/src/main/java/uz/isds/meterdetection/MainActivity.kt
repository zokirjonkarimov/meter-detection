package uz.isds.meterdetection

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import uz.isds.meterdetection.ui.theme.MeterDetectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeterDetectionTheme {
                var startSdk by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = { startSdk = true }) { Text(text = "Strat SDK") }
                }

                LaunchedEffect(startSdk) {
                    if (startSdk) {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                uz.isds.meterai.MainActivity::class.java
                            )
                        )
                        startSdk = false
                    }
                }
            }
        }
    }
}