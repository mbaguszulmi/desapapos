package com.desapabandara.pos.base.eventbus

import com.desapabandara.pos_backend.model.response.StoreResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreLoginEventBus @Inject constructor() {
    private val _currentStore = MutableStateFlow<StoreResponse?>(null)
    val currentStore = _currentStore.asStateFlow()

    fun setCurrentStore(store: StoreResponse?) {
        _currentStore.value = store
    }
}