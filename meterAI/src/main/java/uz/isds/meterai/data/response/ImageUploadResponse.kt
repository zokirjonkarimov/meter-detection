package uz.isds.meterai.data.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageUploadResponse(
    @SerialName("ok")
    val ok: Boolean? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("data")
    val data: Data? = null,
    @SerialName("cropped_image")
    val croppedImage: String? = null
)

@Serializable
data class Data(
    @SerialName("filename")
    val filename: String? = null,
    @SerialName("size")
    val size: Int? = null,
    @SerialName("result")
    val result: List<String?>? = null,
    @SerialName("percent")
    val percent: List<Double?>? = null
)