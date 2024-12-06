package uz.isds.meterai.ui.presenter.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.MutableValue
import uz.isds.meterai.data.response.ImageUploadResponse
import uz.isds.meterai.ui.intent.ResultIntent
import uz.isds.meterai.ui.navigation.RootComponent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.ResultUiState

class ResultPresenterImpl(
    componentContext: ComponentContext,
    private val navigator: StackNavigation<RootComponent.Config>,
    data: ImageUploadResponse
) : CommonPresenter<ResultIntent, ResultUiState>, ComponentContext by componentContext{
    override val uiState = MutableValue(ResultUiState(data = data))

    override fun onEventDispatcher(intent: ResultIntent) {
        when(intent){
            ResultIntent.OpenCamera -> navigator.replaceAll(RootComponent.Config.Camera)
        }
    }
}
