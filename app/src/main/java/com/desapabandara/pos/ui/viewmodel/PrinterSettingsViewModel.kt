package com.desapabandara.pos.ui.viewmodel

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import com.desapabandara.pos.R
import com.desapabandara.pos.local_db.dao.LocationDao
import com.desapabandara.pos.local_db.dao.PrinterDao
import com.desapabandara.pos.model.ui.PrinterDeviceScanResult
import com.desapabandara.pos.model.ui.PrinterDisplay
import com.desapabandara.pos.printer.manager.PrinterManager
import com.desapabandara.pos.printer.model.PaperWidth
import com.desapabandara.pos.ui.fragment.EditPrinterFragment
import com.desapabandara.pos.ui.fragment.ScanPrinterFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterSettingsViewModel @Inject constructor(
    private val printerDao: PrinterDao,
    private val locationDao: LocationDao,
    private val printerManager: PrinterManager,
    private val fragmentStateEventBus: FragmentStateEventBus,
    private val bluetoothAdapter: BluetoothAdapter?,
    private val uiStatusEventBus: UIStatusEventBus,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    val printers = printerDao.getAll().map {
        it.map { printers ->
            with(printers) {
                PrinterDisplay(
                    id,
                    name,
                    address,
                    isConnected
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun addNewPrinter() {
        viewModelScope.launch(ioDispatcher) {
            bluetoothAdapter?.let {
                if (bluetoothAdapter.isEnabled) {
                    awaitPrinterConnectionSelection()
                    fragmentStateEventBus.setCurrentState(ScanPrinterFragment(), true)
                } else {
                    uiStatusEventBus.setUiStatus(UiStatus.ShowError(UiMessage.ResourceMessage(
                            R.string.error_bluetooth_disabled
                        )))
                }
            } ?: uiStatusEventBus.setUiStatus(UiStatus.ShowError(UiMessage.ResourceMessage(
                R.string.error_bluetooth_not_supported
            )))
        }
    }

    private fun awaitPrinterConnectionSelection() {
        viewModelScope.launch(ioDispatcher) {
            val result = fragmentStateEventBus.awaitStateResult<PrinterDeviceScanResult>()

            if (result is PrinterDeviceScanResult.Selected) {
                result.run {
                    val locations = locationDao.getLocations().map {
                        it.id
                    }

                    val printerData = printerManager.addBluetoothPrinter(deviceConnection, deviceConnection.name, PaperWidth.W80, locations)
                    fragmentStateEventBus.setCurrentState(EditPrinterFragment.newInstance(printerData), true)
                }
            }
        }
    }

    fun printTestPage(id: String) {
        printerManager.printTestPage(id)
    }

    fun connectPrinter(id: String) {
        printerManager.reconnectBluetoothPrinter(id)
    }

    fun deletePrinter(id: String) {
        printerManager.deletePrinter(id)
    }

    fun editPrinter(it: String) {
        viewModelScope.launch(ioDispatcher) {
            printerDao.getPrinter(it)?.let {
                fragmentStateEventBus.setCurrentState(EditPrinterFragment.newInstance(it), true)
            }
        }
    }

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished()
    }

}