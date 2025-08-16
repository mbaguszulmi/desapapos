package com.desapabandara.pos.base.manager

import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.AuthEventBus
import com.desapabandara.pos.base.eventbus.StoreLoginEventBus
import com.desapabandara.pos.local_db.dao.StaffDao
import com.desapabandara.pos.local_db.entity.StaffEntity
import com.desapabandara.pos.preference.datastore.AuthDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val authEventBus: AuthEventBus,
    private val storeLoginEventBus: StoreLoginEventBus,
    private val authDataStore: AuthDataStore,
    private val staffDao: StaffDao,
) {
    private val scope: CoroutineScope = CoroutineScope(ioDispatcher + SupervisorJob())

    private val _currentStaff = MutableStateFlow<StaffEntity?>(null)
    val currentStaff = _currentStaff.asStateFlow()

    fun observeLogin() {
        scope.launch {
            _currentStaff.value = staffDao.getStaffByIdSingle("9")

            combine(
                authDataStore.getToken(),
                authDataStore.getRefreshToken(),
                authDataStore.getCurrentStoreData()
            ) { t, rt, s ->
                Triple(t, rt, s)
            }.collect { (token, refreshToken, store) ->
                authEventBus.apply {
                    setCurrentToken(token)
                    setCurrentRefreshToken(refreshToken)
                }

                storeLoginEventBus.setCurrentStore(store)
            }
        }
    }

    fun logout() {
        scope.launch {
            authDataStore.apply {
                deleteToken()
                deleteStoreData()
                deleteRefreshToken()
            }
        }
    }
}
