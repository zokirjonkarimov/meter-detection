package uz.isds.meterai.ui.presenter.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.MutableValue
import uz.isds.meterai.ui.intent.CameraIntent
import uz.isds.meterai.ui.intent.ResultIntent
import uz.isds.meterai.ui.navigation.RootComponent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.CameraUiState
import uz.isds.meterai.ui.uistate.ResultUiState

class ResultPresenterImpl(
    componentContext: ComponentContext,
    navigator: StackNavigation<RootComponent.Config>,
    data: String
) : CommonPresenter<ResultIntent, ResultUiState>, ComponentContext by componentContext{
    override val uiState = MutableValue(ResultUiState())

    override fun onEventDispatcher(intent: ResultIntent) {

    }
}
