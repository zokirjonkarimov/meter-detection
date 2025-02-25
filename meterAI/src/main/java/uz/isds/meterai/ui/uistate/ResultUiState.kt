package uz.isds.meterai.ui.uistate

import uz.isds.meterai.data.response.ImageUploadResponse

data class ResultUiState(
    val data: ImageUploadResponse,
    val percentage : Int = 0
)
