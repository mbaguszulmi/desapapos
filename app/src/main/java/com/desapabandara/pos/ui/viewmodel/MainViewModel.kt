package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import com.desapabandara.pos.base.eventbus.StoreLoginEventBus
import com.desapabandara.pos.base.manager.AuthManager
import com.desapabandara.pos.model.ui.MainMenu
import com.desapabandara.pos.model.ui.MainScreen
import com.desapabandara.pos.preference.datastore.SyncDataStore
import com.desapabandara.pos_backend.model.response.StoreResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val storeLoginEventBus: StoreLoginEventBus,
    private val syncDataStore: SyncDataStore,
    private val authManager: AuthManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _mainScreen = MutableStateFlow(MainScreen.None)
    val mainScreen = _mainScreen.asStateFlow()

    private val _activeMainMenu = MutableStateFlow(MainMenu.None)
    val activeMainMenu = _activeMainMenu.asStateFlow()

    private val _showMenuDrawer = MutableStateFlow(false)
    val showMenuDrawer = _showMenuDrawer.asStateFlow()

    init {
        observeAuthStatus()
    }

    private fun observeAuthStatus() {
        viewModelScope.launch(ioDispatcher) {
            storeLoginEventBus.currentStore.flatMapLatest {
                if (it == null) {
                    syncDataStore.setSyncStatus(false)
                    flow<Pair<StoreResponse?, Boolean>> {
                        emit(null to false)
                    }
                } else {
                    syncDataStore.getSyncStatus().map { syncStatus ->
                        it to syncStatus
                    }
                }
            }.flowOn(ioDispatcher).collect { (store, isSynced) ->
                _mainScreen.value = when {
                    store == null -> {
                         MainScreen.Login
                    }
                    !isSynced -> {
                        MainScreen.Sync
                    }
                    else -> {
                        selectMainMenu(MainMenu.POS)
                        MainScreen.Main
                    }
                }
            }
        }
    }

    fun selectMainMenu(menu: MainMenu) {
        viewModelScope.launch(ioDispatcher) {
            _activeMainMenu.value = menu
            closeMenuDrawer()
        }
    }

    fun systemLogout() {
        viewModelScope.launch(ioDispatcher) {
            authManager.logout()
            _activeMainMenu.value = MainMenu.None
            closeMenuDrawer()
        }
    }

    fun toggleMenuDrawer() {
        viewModelScope.launch(ioDispatcher) {
            _showMenuDrawer.value = !_showMenuDrawer.value
        }
    }

    fun closeMenuDrawer() {
        viewModelScope.launch(ioDispatcher) {
            _showMenuDrawer.value = false
        }
    }

    fun reSyncData() {
        viewModelScope.launch(ioDispatcher) {
            syncDataStore.setSyncStatus(false)
            _activeMainMenu.value = MainMenu.None
        }
    }
}