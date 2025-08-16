package com.desapabandara.pos.printer.model

import com.dantsu.escposprinter.connection.DeviceConnection

data class PrinterConnection(
    val connection: DeviceConnection,
    val address: String,
    val name: String
)
