package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.model.ui.PrinterDeviceScanResult
import com.desapabandara.pos.printer.manager.PrinterManager
import com.desapabandara.pos.printer.model.PrinterDeviceScanDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ScanPrinterViewModel @Inject constructor(
    private val printerManager: PrinterManager,
    private val fragmentStateEventBus: FragmentStateEventBus
): ViewModel() {
    val printerDevices = printerManager.scanBluetoothDevices().map {
        it.map { devices ->
            devices.run {
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
        fragmentStateEventBus.currentStateFinished()
    }
}