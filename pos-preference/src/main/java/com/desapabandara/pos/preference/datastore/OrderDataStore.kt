package com.desapabandara.pos.preference.datastore

import co.mbznetwork.android.base.storage.AppDataStore
import com.desapabandara.pos.preference.LAST_ORDER_NUMBER_KEY
import com.desapabandara.pos.preference.ORDER_START_DATE_KEY
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderDataStore @Inject constructor(
    private val appDataStore: AppDataStore
) {
    suspend fun storeLastOrderNumber(number: Int) {
        appDataStore.editData(LAST_ORDER_NUMBER_KEY, number)
    }

    fun getLastOrderNumber() = appDataStore.getDataDefault(LAST_ORDER_NUMBER_KEY, 0)

    suspend fun storeOrderStartDate(dateStr: String) {
        appDataStore.editData(ORDER_START_DATE_KEY, dateStr)
    }

    fun getOrderStartDate() = appDataStore.getData(ORDER_START_DATE_KEY, String::class.java)
}
