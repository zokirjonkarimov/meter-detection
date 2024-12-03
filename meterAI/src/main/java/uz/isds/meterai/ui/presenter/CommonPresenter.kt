package uz.isds.meterai.ui.presenter

import com.arkivanov.decompose.value.Value

interface CommonPresenter<I, U : Any> {
    val uiState: Value<U>
    fun onEventDispatcher(intent: I)
}