package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.PrinterTemplateEntity

@Dao
abstract class PrinterTemplateDao: BaseDao<PrinterTemplateEntity>("PrinterTemplateEntity") {
    @Query("SELECT * FROM PrinterTemplateEntity WHERE id = :id")
    abstract suspend fun getPrinterTemplate(id: String): PrinterTemplateEntity?
}