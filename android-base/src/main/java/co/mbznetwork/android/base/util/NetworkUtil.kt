package co.mbznetwork.android.base.util

import co.mbznetwork.android.base.model.network.NetworkResult
import timber.log.Timber

suspend fun <T, U, V> handleOnlineData(
    request: U,
    block: suspend (U) -> NetworkResult<T>,
    success: suspend (T) -> V,
    error: suspend (Int, String) -> V? = { code, message ->
        Timber.e("Error in backend data: $code, message: $message")
        null
    }
): V? = when (val response = block(request)) {
    is NetworkResult.Success -> {
        response.data?.let { data ->
            success(data)
        }
    }

    is NetworkResult.Error -> {
        Timber.e("Network error when sync data: ${response.message}")
        error(response.code, response.message.toString())
    }
}