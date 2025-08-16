package co.mbznetwork.android.base.model.network

sealed class NetworkResult<out T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String?, data: T? = null, val code: Int = -1) : NetworkResult<T>(message = message, data = data)
}
