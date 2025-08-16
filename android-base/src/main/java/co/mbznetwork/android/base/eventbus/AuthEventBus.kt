package co.mbznetwork.android.base.eventbus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthEventBus @Inject constructor() {
    private val _currentToken = MutableStateFlow("")
    val currentToken = _currentToken.asStateFlow()

    private val _currentRefreshToken = MutableStateFlow("")
    val currentRefreshToken = _currentRefreshToken.asStateFlow()

    fun setCurrentToken(token: String) {
        _currentToken.value = token
    }

    fun setCurrentRefreshToken(token: String) {
        _currentRefreshToken.value = token
    }
}