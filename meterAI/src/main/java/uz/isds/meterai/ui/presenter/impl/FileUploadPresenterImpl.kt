package uz.isds.meterai.ui.presenter.impl

import android.graphics.Bitmap
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnPause
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.isds.meterai.domain.AiRepository
import uz.isds.meterai.domain.impl.AiRepositoryImpl
import uz.isds.meterai.other.BoundingBox
import uz.isds.meterai.other.Detector
import uz.isds.meterai.other.cropBitmap
import uz.isds.meterai.ui.intent.FileUploadIntent
import uz.isds.meterai.ui.navigation.RootComponent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.FileUploadUiState

class FileUploadPresenterImpl(
    componentContext: ComponentContext,
    private val navigator: StackNavigation<RootComponent.Config>
) : CommonPresenter<FileUploadIntent, FileUploadUiState>,ComponentContext by componentContext{
    override val uiState = MutableValue(FileUploadUiState())
    private val repository : AiRepository = AiRepositoryImpl()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        doOnDestroy { scope.cancel() }
    }

    @OptIn(DelicateDecomposeApi::class)
    override fun onEventDispatcher(intent: FileUploadIntent) {
        when(intent){
            FileUploadIntent.OpenCamera -> navigator.push(RootComponent.Config.Camera)
            is FileUploadIntent.DetectBitmap -> {
                uiState.update { it.copy(loading = true) }
                scope.launch {
                    intent.detector.onDetect(object : Detector.DetectorListener {
                        override fun onEmptyDetect() {
                            uiState.update { it.copy(errorChooseImage = true, loading = false) }
                        }

                        override fun onDetect(boundingBoxes: List<BoundingBox>, bitmap: Bitmap) {
                            navigator.push(RootComponent.Config.ImageConfirm(cropBitmap(bitmap,boundingBoxes.maxBy { it.cnf })))
                            uiState.update { it.copy(loading = false, openBottomSheet = true) }
                        }
                    })
                    intent.bitmap?.let {
                        intent.detector.detect(intent.bitmap)
                    }
                }
            }

            FileUploadIntent.ToastHide -> uiState.update { it.copy(errorChooseImage = false) }
            FileUploadIntent.Dismiss -> uiState.update { it.copy(openBottomSheet = false) }
        }
    }
}
