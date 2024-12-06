package uz.isds.meterai.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import uz.isds.meterai.ui.intent.CameraIntent
import uz.isds.meterai.ui.intent.ImageConfirmIntent
import uz.isds.meterai.ui.intent.ResultIntent
import uz.isds.meterai.ui.intent.SendImageIntent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.presenter.impl.CameraPresenterImpl
import uz.isds.meterai.ui.presenter.impl.ImageConfirmPresenterImpl
import uz.isds.meterai.ui.presenter.impl.ResultPresenterImpl
import uz.isds.meterai.ui.presenter.impl.SendImagePresenterImpl
import uz.isds.meterai.ui.uistate.CameraUiState
import uz.isds.meterai.ui.uistate.ImageConfirmUiState
import uz.isds.meterai.ui.uistate.ResultUiState
import uz.isds.meterai.ui.uistate.SendImageUiState

class RootComponentImpl(componentContext: ComponentContext) : RootComponent,
    ComponentContext by componentContext {
    private val navigator = StackNavigation<RootComponent.Config>()
    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigator,
            serializer = RootComponent.Config.serializer(),
            initialConfiguration = RootComponent.Config.Camera,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(
        config: RootComponent.Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            RootComponent.Config.Camera -> {
                val presenter: CommonPresenter<CameraIntent, CameraUiState> =
                    CameraPresenterImpl(componentContext, navigator)
                RootComponent.Child.Camera(presenter)
            }

            is RootComponent.Config.ImageConfirm -> {
                val presenter: CommonPresenter<ImageConfirmIntent, ImageConfirmUiState> =
                    ImageConfirmPresenterImpl(componentContext, navigator,config.bitmap)
                RootComponent.Child.ImageConfirm(presenter)
            }
            is RootComponent.Config.Result -> {
                val presenter: CommonPresenter<ResultIntent, ResultUiState> =
                    ResultPresenterImpl(componentContext, navigator,config.data)
                RootComponent.Child.Result(presenter)
            }
            is RootComponent.Config.SendImage -> {
                val presenter: CommonPresenter<SendImageIntent, SendImageUiState> =
                    SendImagePresenterImpl(componentContext, navigator,config.byteArray)
                RootComponent.Child.SendImage(presenter)
            }
        }
    }
}