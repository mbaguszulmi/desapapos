package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import co.mbznetwork.android.base.util.handleOnlineData
import com.desapabandara.pos.preference.datastore.AuthDataStore
import com.desapabandara.pos.base.repository.OnlineRepository
import com.desapabandara.pos_backend.model.request.PosLoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val onlineRepository: OnlineRepository,
    private val uiStatusEventBus: UIStatusEventBus,
    private val authDataStore: AuthDataStore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    val username = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun login() {
        viewModelScope.launch(ioDispatcher) {
            handleOnlineData(
                PosLoginRequest(username.value, password.value),
                {
                    uiStatusEventBus.setUiStatus(UiStatus.Loading)
                    onlineRepository.posLogin(it)
                }, { result ->
                    authDataStore.run {
                        storeToken(result.token)
                        storeRefreshToken(result.refreshToken)
                        storeCurrentStoreData(result.store)
                    }
                    uiStatusEventBus.setUiStatus(UiStatus.Idle)
                }, { code, message ->
                    uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                        UiMessage.StringMessage("$message ($code)")
                    ))
                }
            )
        }
    }
}
