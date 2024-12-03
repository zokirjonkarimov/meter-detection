package uz.isds.meterai.ui.intent

sealed interface ImageConfirmIntent {
    data object Back : ImageConfirmIntent
    data object Done : ImageConfirmIntent
}
