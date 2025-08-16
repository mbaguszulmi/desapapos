package com.desapabandara.pos.model.ui

import com.desapabandara.pos.printer.model.PrinterConnection

sealed class PrinterDeviceScanResult {
    data object Cancelled: PrinterDeviceScanResult()

    data class Selected(
        val deviceConnection: PrinterConnection
    ): PrinterDeviceScanResult()
}
