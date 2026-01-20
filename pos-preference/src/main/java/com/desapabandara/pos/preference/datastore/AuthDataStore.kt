package com.desapabandara.pos.preference.datastore

import co.mbznetwork.android.base.storage.AppDataStore
import com.desapabandara.pos.preference.AUTH_LOGGED_IN_STORE
import com.desapabandara.pos.preference.AUTH_REFRESH_TOKEN_KEY
import com.desapabandara.pos.preference.AUTH_STORE_LOGO_HEX
import com.desapabandara.pos.preference.AUTH_TOKEN_KEY
import com.desapabandara.pos_backend.model.response.StoreResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataStore @Inject constructor(
    private val appDataStore: AppDataStore,
) {
    suspend fun storeToken(token: String) {
        appDataStore.editData(AUTH_TOKEN_KEY, token)
    }

    fun getToken() = appDataStore.getDataDefault(AUTH_TOKEN_KEY, "")

    suspend fun deleteToken() = appDataStore.removeData(AUTH_TOKEN_KEY, String::class.java)

    suspend fun storeRefreshToken(refreshToken: String) {
        appDataStore.editData(AUTH_REFRESH_TOKEN_KEY, refreshToken)
    }

    fun getRefreshToken() = appDataStore.getDataDefault(AUTH_REFRESH_TOKEN_KEY, "")

    suspend fun deleteRefreshToken() = appDataStore.removeData(AUTH_REFRESH_TOKEN_KEY, String::class.java)

    suspend fun storeCurrentStoreData(store: StoreResponse) {
        appDataStore.editData(AUTH_LOGGED_IN_STORE, store)
    }

    fun getCurrentStoreData() = appDataStore.getData(AUTH_LOGGED_IN_STORE, StoreResponse::class.java)

    suspend fun deleteStoreData() = appDataStore.removeData(AUTH_LOGGED_IN_STORE, StoreResponse::class.java)

    suspend fun saveStoreLogoBase64(logo: String) = appDataStore.editData(AUTH_STORE_LOGO_HEX, logo)

    fun getStoreLogoBase64() = appDataStore.getDataDefault(AUTH_STORE_LOGO_HEX, "")

    suspend fun deleteStoreLogoBase64() = appDataStore.removeData(AUTH_STORE_LOGO_HEX, String::class.java)
}
