package uz.isds.meterai.domain

import kotlinx.coroutines.flow.Flow
import uz.isds.meterai.data.ResultData
import uz.isds.meterai.data.response.ImageUploadResponse
import java.io.File

interface Repository {
    fun sendImage(byteArray: ByteArray): Flow<ResultData<ImageUploadResponse>>
}