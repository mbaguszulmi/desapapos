package com.desapabandara.pos.printer.model

import com.desapabandara.pos.base.model.Order
import com.desapabandara.pos.local_db.entity.LocationEntity
import com.desapabandara.pos.local_db.entity.PrinterTemplateEntity
import java.util.Date

data class PrintTask(
    val location: LocationEntity,
    val printerDevice: PrinterDevice,
    val order: Order,
    val reprint: Boolean,
    val schedulePrint: Date?,
    val printerTemplate: PrinterTemplateEntity
)
