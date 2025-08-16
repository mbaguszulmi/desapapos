package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.LocationEntity
import com.desapabandara.pos.local_db.entity.PrinterLocationEntity

@Dao
abstract class PrinterLocationDao: BaseDao<PrinterLocationEntity>("PrinterLocationEntity") {
    @Query("SELECT * FROM LocationEntity WHERE id IN (SELECT locationId FROM PrinterLocationEntity WHERE printerId = :id)")
    abstract suspend fun getPrinterLocationFromPrinter(id: String): List<LocationEntity>
}