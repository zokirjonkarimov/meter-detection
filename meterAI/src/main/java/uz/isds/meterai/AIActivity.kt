package uz.isds.meterai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import uz.isds.meterai.domain.impl.apiKey
import uz.isds.meterai.ui.CameraScreen
import uz.isds.meterai.ui.ImageConfirmScreen
import uz.isds.meterai.ui.ResultScreen
import uz.isds.meterai.ui.SendImageScreen
import uz.isds.meterai.ui.navigation.RootComponent
import uz.isds.meterai.ui.navigation.RootComponentImpl

class AIActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootComponent : RootComponent = RootComponentImpl(defaultComponentContext())
        apiKey = intent.getStringExtra("x-api-key") ?: ""
        setContent {
            Children(
                stack = rootComponent.stack,
                animation = stackAnimation(fade()),
                modifier = Modifier.fillMaxSize()
            ) {
                when(val instance = it.instance){
                    is RootComponent.Child.Camera -> CameraScreen(instance.presenter)
                    is RootComponent.Child.ImageConfirm -> ImageConfirmScreen(instance.presenter)
                    is RootComponent.Child.Result -> ResultScreen(instance.presenter)
                    is RootComponent.Child.SendImage -> SendImageScreen(instance.presenter)
                }
            }
        }
    }
}
