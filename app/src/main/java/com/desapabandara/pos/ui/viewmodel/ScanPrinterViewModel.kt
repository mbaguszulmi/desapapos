package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.local_db.dao.PrinterDao
import com.desapabandara.pos.model.ui.PrinterDeviceScanResult
import com.desapabandara.pos.printer.manager.PrinterManager
import com.desapabandara.pos.printer.model.PrinterDeviceScanDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ScanPrinterViewModel @Inject constructor(
    printerManager: PrinterManager,
    printerDao: PrinterDao,
    private val fragmentStateEventBus: FragmentStateEventBus
): ViewModel() {
    val printerDevices = combine(
        printerManager.scanBluetoothDevices(),
        printerDao.getAll()
    ) { devices, printers ->
        devices.filter { d ->
            printers.find {
                d.address == it.address
            } == null
        }.map { d ->
            d.run {
                PrinterDeviceScanDisplay(
                    address,
                    name,
                    this
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectDevice(device: PrinterDeviceScanDisplay) {
        fragmentStateEventBus.currentStateFinished(PrinterDeviceScanResult.Selected(device.connection))
    }

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished(PrinterDeviceScanResult.Cancelled)
    }
}