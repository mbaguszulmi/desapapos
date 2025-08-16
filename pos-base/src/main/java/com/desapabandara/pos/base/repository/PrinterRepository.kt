package com.desapabandara.pos.base.repository

import com.desapabandara.pos.local_db.dao.LocationDao
import com.desapabandara.pos.local_db.dao.PrinterDao
import com.desapabandara.pos.local_db.dao.PrinterTemplateDao
import com.desapabandara.pos.local_db.entity.LocationEntity
import com.desapabandara.pos.local_db.entity.PrinterTemplateEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrinterRepository @Inject constructor(
    private val printerTemplateDao: PrinterTemplateDao,
    private val locationDao: LocationDao,
    private val printerDao: PrinterDao
) {
    suspend fun savePrinterTemplates(templates: List<PrinterTemplateEntity>) {
        printerTemplateDao.insertMany(templates)
    }

    suspend fun saveLocations(locations: List<LocationEntity>) {
        locationDao.insertMany(locations)
    }
 }