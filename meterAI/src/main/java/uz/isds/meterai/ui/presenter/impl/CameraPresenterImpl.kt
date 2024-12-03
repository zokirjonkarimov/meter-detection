package uz.isds.meterai.ui.presenter.impl

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.MutableValue
import uz.isds.meterai.ui.intent.CameraIntent
import uz.isds.meterai.ui.navigation.RootComponent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.CameraUiState

class CameraPresenterImpl(
    componentContext: ComponentContext,
    private val navigator: StackNavigation<RootComponent.Config>
) : CommonPresenter<CameraIntent,CameraUiState>, ComponentContext by componentContext{
    override val uiState = MutableValue(CameraUiState())

    @OptIn(DelicateDecomposeApi::class)
    override fun onEventDispatcher(intent: CameraIntent) {
        when(intent){
            is CameraIntent.OnCupture -> {
                navigator.push(RootComponent.Config.ImageConfirm(intent.bitmap))
            }
        }
    }
}