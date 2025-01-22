package uz.isds.meterai.util

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


fun Throwable.isConnection(): Boolean {
    return when (this) {
        is ConnectException -> true
        is HttpRequestTimeoutException -> true
        is ConnectTimeoutException -> true
        is SocketTimeoutException -> true
        is UnknownHostException -> true
        else -> false
    }
}