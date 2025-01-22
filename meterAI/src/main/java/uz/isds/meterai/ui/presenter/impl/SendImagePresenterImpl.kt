package uz.isds.meterai.ui.presenter.impl

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.isds.meterai.data.Message
import uz.isds.meterai.data.ResultData
import uz.isds.meterai.domain.AiRepository
import uz.isds.meterai.domain.impl.AiRepositoryImpl
import uz.isds.meterai.ui.intent.SendImageIntent
import uz.isds.meterai.ui.navigation.RootComponent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.SendImageUiState
import uz.isds.meterai.util.isConnection

@OptIn(DelicateDecomposeApi::class)
class SendImagePresenterImpl(
    componentContext: ComponentContext,
    private val navigator: StackNavigation<RootComponent.Config>,
    private val byteArray: ByteArray?
) : CommonPresenter<SendImageIntent, SendImageUiState>, ComponentContext by componentContext {
    override val uiState = MutableValue(SendImageUiState())
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val aiRepository: AiRepository = AiRepositoryImpl()

    init {
        doOnDestroy { coroutineScope.cancel() }
//        coroutineScope.launch {
//            delay(2000)
//            navigator.push(RootComponent.Config.Result(""))
//        }
        if (byteArray != null) {
            aiRepository.sendImage(byteArray).onEach { result ->
                when (result) {
                    is ResultData.Error -> {
                        if (result.error.isConnection()) {
                            uiState.update { it.copy(message = Message("Connection error",-1)) }
                        }else{
                            uiState.update { it.copy(message = Message(result.error.message,-5)) }
                        }
                    }

                    is ResultData.Message -> uiState.update {
                        it.copy(
                            message = Message(
                                message = result.message,
                                result.code
                            )
                        )
                    }

                    is ResultData.Success -> navigator.push(RootComponent.Config.Result(result.data))
                }
            }.launchIn(coroutineScope)
        }
    }

    override fun onEventDispatcher(intent: SendImageIntent) {
        when (intent) {
            SendImageIntent.Back -> navigator.pop()
            SendImageIntent.ToastHide -> uiState.update { it.copy(message = null) }
        }
    }
}
