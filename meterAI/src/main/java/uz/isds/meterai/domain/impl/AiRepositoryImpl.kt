package uz.isds.meterai.domain.impl

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import uz.isds.meterai.data.ResultData
import uz.isds.meterai.data.response.ImageUploadResponse
import uz.isds.meterai.domain.AiRepository

class AiRepositoryImpl : AiRepository {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    encodeDefaults = true
                }
            )
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.v("Logger Ktor =>", message)
                }
            }
            level = LogLevel.ALL
        }

        install(ResponseObserver) {
            onResponse { response ->
                Log.d("HTTP status:", "${response.status.value}")
            }
        }

        defaultRequest {
            url("http://10.1.2.234:27000/")
            headers {
                append("x-api-key","TqfrxrvI0rkLed7BF3fmJYr0yWmLHuTVxJAr5tWxuWIJ80hmM2PsFYPspRaAPPDZ")
            }
        }
    }

    override fun sendImage(byteArray: ByteArray) : Flow<ResultData<ImageUploadResponse>> {
        return flow {
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = "upload",
                formData = formData {
                    append(
                        key = "file",
                        value = byteArray,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
                        }
                    )
                }
            )
            val responseBody = response.body<ImageUploadResponse>()
            if (responseBody.ok == true){
                emit(ResultData.Success(responseBody))
            }else{
                emit(ResultData.Message(responseBody.message))
            }
        }.catch {
            emit(ResultData.Error(it))
        }.flowOn(Dispatchers.IO)
    }
}