package co.mbznetwork.android.base

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import co.mbznetwork.android.base.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStatusManager @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())
    private val _hasInternet = MutableStateFlow(false)
    val hasInternet = _hasInternet.asStateFlow()

    fun start() {
        scope.launch {
            while (true) {
                _hasInternet.value = isInternetAvailable()

                delay(1000)
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork

        if (activeNetwork != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (networkCapabilities != null) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                return hasInternet && isValidated
            }
        }

        return false
    }

    fun stop() {
        scope.cancel()
    }
}