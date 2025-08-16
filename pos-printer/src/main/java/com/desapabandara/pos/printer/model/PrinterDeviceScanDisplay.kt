package com.desapabandara.pos.printer.model

data class PrinterDeviceScanDisplay(
    val address: String,
    val name: String,
    val connection: PrinterConnection
)
