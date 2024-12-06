package uz.isds.meterai.data

sealed interface ResultData<out T> {
    class Error<T>(val error : Throwable) : ResultData<T>
    class Success<T>(val data : T) : ResultData<T>
    class Message<T>(val message: String? = null, val code : Int? = null) : ResultData<T>
}