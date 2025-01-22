package uz.isds.meterai.ui.intent

sealed interface SendImageIntent {
    data object Back : SendImageIntent
    data object ToastHide : SendImageIntent
}
