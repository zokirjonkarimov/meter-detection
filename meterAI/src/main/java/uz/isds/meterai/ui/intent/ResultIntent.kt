package uz.isds.meterai.ui.intent

sealed interface ResultIntent {
    data object OpenCamera : ResultIntent

}
