package uz.isds.meterai.domain

import kotlinx.coroutines.flow.Flow
import uz.isds.meterai.data.ResultData
import uz.isds.meterai.data.response.ImageUploadResponse

interface AiRepository {
    fun sendImage(byteArray: ByteArray): Flow<ResultData<ImageUploadResponse>>
}