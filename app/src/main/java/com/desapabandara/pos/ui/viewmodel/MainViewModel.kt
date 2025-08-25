package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import com.desapabandara.pos.base.eventbus.StaffLoginEventBus
import com.desapabandara.pos.base.eventbus.StoreLoginEventBus
import com.desapabandara.pos.base.manager.AuthManager
import com.desapabandara.pos.local_db.entity.StaffEntity
import com.desapabandara.pos.model.ui.MainMenu
import com.desapabandara.pos.model.ui.MainScreen
import com.desapabandara.pos.preference.datastore.SyncDataStore
import com.desapabandara.pos_backend.model.response.StoreResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val storeLoginEventBus: StoreLoginEventBus,
    private val syncDataStore: SyncDataStore,
    private val authManager: AuthManager,
    private val staffLoginEventBus: StaffLoginEventBus,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _mainScreen = MutableStateFlow(MainScreen.None)
    val mainScreen = _mainScreen.asStateFlow()

    private val _activeMainMenu = MutableStateFlow(MainMenu.None)
    val activeMainMenu = _activeMainMenu.asStateFlow()

    private val _showMenuDrawer = MutableStateFlow(false)
    val showMenuDrawer = _showMenuDrawer.asStateFlow()

    val staffName = staffLoginEventBus.currentStaff.map {
        it?.name ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    init {
        observeAuthStatus()
    }

    private fun observeAuthStatus() {
        viewModelScope.launch(ioDispatcher) {
            storeLoginEventBus.currentStore.flatMapLatest {
                if (it == null) {
                    syncDataStore.setSyncStatus(false)
                    flow<Triple<StoreResponse?, Boolean, StaffEntity?>> {
                        emit(Triple(null, false, null))
                    }
                } else {
                    combine(
                        syncDataStore.getSyncStatus(),
                        staffLoginEventBus.currentStaff
                    ) { syncStatus, staff ->
                        Triple(it, syncStatus, staff)
                    }
                }
            }.flowOn(ioDispatcher).collect { (store, isSynced, staff) ->
                _mainScreen.value = when {
                    store == null -> {
                         MainScreen.Login
                    }
                    !isSynced -> {
                        MainScreen.Sync
                    }
                    staff == null -> {
                        selectMainMenu(MainMenu.None)
                        MainScreen.StaffLogin
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
            staffLoginEventBus.setCurrentStaff(null)
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

    fun changeStaff() {
        viewModelScope.launch(ioDispatcher) {
            staffLoginEventBus.setCurrentStaff(null)
        }
    }
}