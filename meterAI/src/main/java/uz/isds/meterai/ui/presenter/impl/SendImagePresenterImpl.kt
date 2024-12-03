package uz.isds.meterai.ui.presenter.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.isds.meterai.ui.intent.SendImageIntent
import uz.isds.meterai.ui.navigation.RootComponent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.SendImageUiState

@OptIn(DelicateDecomposeApi::class)
class SendImagePresenterImpl(
    componentContext: ComponentContext,
    navigator: StackNavigation<RootComponent.Config>
) : CommonPresenter<SendImageIntent, SendImageUiState>, ComponentContext by componentContext{
    override val uiState = MutableValue(SendImageUiState())
    private val coroutineScope = CoroutineScope(Dispatchers.Main+ SupervisorJob())

    init {
        doOnDestroy { coroutineScope.cancel() }
        coroutineScope.launch {
            delay(2000)
            navigator.push(RootComponent.Config.Result(""))
        }
    }

    override fun onEventDispatcher(intent: SendImageIntent) {

    }
}
