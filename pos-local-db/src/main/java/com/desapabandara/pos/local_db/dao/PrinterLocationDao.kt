package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.LocationEntity
import com.desapabandara.pos.local_db.entity.PrinterLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PrinterLocationDao: BaseDao<PrinterLocationEntity>("PrinterLocationEntity") {
    @Query("SELECT * FROM LocationEntity WHERE id IN (SELECT locationId FROM PrinterLocationEntity WHERE printerId = :id)")
    abstract suspend fun getPrinterLocationFromPrinter(id: String): List<LocationEntity>

    @Query("SELECT * FROM PrinterLocationEntity WHERE printerId = :id")
    abstract fun getPrinterLocationsOfPrinter(id: String): Flow<List<PrinterLocationEntity>>

    @Query("DELETE FROM PrinterLocationEntity WHERE id = :id")
    abstract suspend fun delete(id: String)
}