package com.desapabandara.pos.printer.model

import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.connection.DeviceConnection
import com.desapabandara.pos.local_db.entity.LocationEntity
import com.desapabandara.pos.local_db.entity.PrinterEntity

data class PrinterDevice(
    val deviceConnection: DeviceConnection,
    val printer: EscPosPrinterCommands,
    val printerData: PrinterEntity,
    val locations: List<LocationEntity>
)
