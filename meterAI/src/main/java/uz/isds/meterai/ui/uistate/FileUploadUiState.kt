package uz.isds.meterai.ui.uistate

data class FileUploadUiState(
    val openBottomSheet: Boolean = true,
    val errorChooseImage: Boolean = false,
    val loading : Boolean = false
)