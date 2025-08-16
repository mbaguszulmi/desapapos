package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.PrinterEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PrinterDao: BaseDao<PrinterEntity>("PrinterEntity") {
    @Query("SELECT * FROM PrinterEntity WHERE updatedAt > :date")
    abstract fun getPrinterAfterDate(date: Long): Flow<List<PrinterEntity>>

    @Query("SELECT * FROM PrinterEntity WHERE id = :id")
    abstract suspend fun getPrinter(id: String): PrinterEntity?

    @Query("DELETE FROM PrinterEntity WHERE id = :id")
    abstract suspend fun deletePrinter(id: String)
}
