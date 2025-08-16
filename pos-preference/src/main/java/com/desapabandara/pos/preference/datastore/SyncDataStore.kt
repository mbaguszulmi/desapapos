package com.desapabandara.pos.preference.datastore

import co.mbznetwork.android.base.storage.AppDataStore
import com.desapabandara.pos.preference.SYNC_STATUS_KEY
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncDataStore @Inject constructor(
    private val appDataStore: AppDataStore
) {
    suspend fun setSyncStatus(synced: Boolean) {
        appDataStore.editData(SYNC_STATUS_KEY, synced)
    }

    fun getSyncStatus(): Flow<Boolean> {
        Timber.d("This is boolean type ${Boolean::class.java == false::class.java}")
        return appDataStore.getDataDefault(SYNC_STATUS_KEY, false)
    }

}