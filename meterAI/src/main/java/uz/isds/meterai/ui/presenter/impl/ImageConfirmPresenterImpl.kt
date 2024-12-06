package uz.isds.meterai.ui.presenter.impl

import android.graphics.Bitmap
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.MutableValue
import uz.isds.meterai.other.bitmapToByteArray
import uz.isds.meterai.ui.intent.ImageConfirmIntent
import uz.isds.meterai.ui.navigation.RootComponent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.ImageConfirmUiState

class ImageConfirmPresenterImpl(
    componentContext: ComponentContext,
    private val navigator: StackNavigation<RootComponent.Config>,
    private val bitmap: Bitmap?
) : CommonPresenter<ImageConfirmIntent, ImageConfirmUiState>,ComponentContext by componentContext {
    override val uiState = MutableValue(ImageConfirmUiState(bitmap = bitmap!!))

    @OptIn(DelicateDecomposeApi::class)
    override fun onEventDispatcher(intent: ImageConfirmIntent) {
        when(intent){
            ImageConfirmIntent.Back -> navigator.pop()
            ImageConfirmIntent.Done -> {
                bitmap?.let {
                    navigator.push(RootComponent.Config.SendImage(bitmap.bitmapToByteArray()))
                }
            }
        }
    }
}
