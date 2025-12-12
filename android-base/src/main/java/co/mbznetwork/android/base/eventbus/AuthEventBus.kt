package co.mbznetwork.android.base.eventbus

import co.mbznetwork.android.base.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthEventBus @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private val scope: CoroutineScope = CoroutineScope(ioDispatcher + SupervisorJob())

    private val _currentToken = MutableStateFlow("")
    val currentToken = _currentToken.asStateFlow()

    private val _logoutRequest = MutableSharedFlow<Boolean>()
    val logoutRequest = _logoutRequest.asSharedFlow()

    private val _currentRefreshToken = MutableStateFlow("")
    val currentRefreshToken = _currentRefreshToken.asStateFlow()

    fun setCurrentToken(token: String) {
        _currentToken.value = token
    }

    fun setCurrentRefreshToken(token: String) {
        _currentRefreshToken.value = token
    }

    fun requestLogout() {
        scope.launch {
            _logoutRequest.emit(true)
        }
    }
}