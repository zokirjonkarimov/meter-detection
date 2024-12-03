package uz.isds.meterai.ui.navigation

import android.graphics.Bitmap
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import uz.isds.meterai.ui.intent.CameraIntent
import uz.isds.meterai.ui.intent.ImageConfirmIntent
import uz.isds.meterai.ui.intent.ResultIntent
import uz.isds.meterai.ui.intent.SendImageIntent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.CameraUiState
import uz.isds.meterai.ui.uistate.ImageConfirmUiState
import uz.isds.meterai.ui.uistate.ResultUiState
import uz.isds.meterai.ui.uistate.SendImageUiState

interface RootComponent {

    sealed interface Child {
        class Camera(val presenter: CommonPresenter<CameraIntent, CameraUiState>) : Child
        class ImageConfirm(val presenter: CommonPresenter<ImageConfirmIntent, ImageConfirmUiState>) :
            Child

        class SendImage(val presenter: CommonPresenter<SendImageIntent, SendImageUiState>) : Child
        class Result(val presenter: CommonPresenter<ResultIntent, ResultUiState>) : Child
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object Camera : Config

        @Serializable
        class ImageConfirm(@Transient val bitmap: Bitmap? = null) : Config

        @Serializable
        class SendImage(@Transient val byteArray: ByteArray? = null) : Config

        @Serializable
        class Result(val data: String) : Config
    }

    val stack: Value<ChildStack<*, Child>>
}