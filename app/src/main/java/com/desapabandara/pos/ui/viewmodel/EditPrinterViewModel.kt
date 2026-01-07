package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.local_db.dao.LocationDao
import com.desapabandara.pos.local_db.dao.PrinterDao
import com.desapabandara.pos.local_db.dao.PrinterLocationDao
import com.desapabandara.pos.local_db.entity.PrinterEntity
import com.desapabandara.pos.local_db.entity.PrinterLocationEntity
import com.desapabandara.pos.model.ui.PrinterLocationSelection
import com.desapabandara.pos.printer.model.PaperWidth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

const val ARG_PRINTER_DATA = "printer_data_key"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EditPrinterViewModel @Inject constructor(
    private val printerDao: PrinterDao,
    private val locationDao: LocationDao,
    private val printerLocationDao: PrinterLocationDao,
    private val fragmentStateEventBus: FragmentStateEventBus,
    savedStateHandle: SavedStateHandle,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    val printerData = savedStateHandle.getStateFlow(ARG_PRINTER_DATA, PrinterEntity(
            id = "",
            name = "",
            address = "",
            paperWidth = PaperWidth.W80.width,
            isConnected = false
        )
    ).stateIn(viewModelScope, SharingStarted.Eagerly, PrinterEntity(
        id = "",
        name = "",
        address = "",
        paperWidth = PaperWidth.W80.width,
        isConnected = false
    ))

    val locations = printerData.filterNot { it.id.isBlank() }.flatMapLatest {
        combine(
            locationDao.getAll(),
            printerLocationDao.getPrinterLocationsOfPrinter(it.id)
        ) { locations, printerLocations ->
            locations.map { location ->
                val printerLocation = printerLocations.find { pl ->
                    pl.locationId == location.id
                }
                PrinterLocationSelection(
                    location.id,
                    location.name,
                    printerLocation?.id ?: ""
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val isAllLocationSelected = locations.map {
        it.all { locationSelection ->
            locationSelection.printerLocationId.isNotBlank()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun selectPaperWidth(paperWidth: PaperWidth) {
        viewModelScope.launch(ioDispatcher) {
            printerData.value.paperWidth = paperWidth.width
        }
    }

    fun toggleSelection(printerLocationSelection: PrinterLocationSelection) {
        viewModelScope.launch(ioDispatcher) {
            if (printerLocationSelection.printerLocationId.isBlank()) {
                printerLocationDao.save(
                    PrinterLocationEntity(
                        id = UUID.randomUUID().toString(),
                        printerId = printerData.value.id,
                        locationId = printerLocationSelection.id
                    )
                )
            } else {
                printerLocationDao.delete(printerLocationSelection.printerLocationId)
            }
        }
    }

    fun toggleSelectAllLocations() {
        viewModelScope.launch(ioDispatcher) {
            val selectAll = !isAllLocationSelected.value
            locations.value.forEach { locationSelection ->
                if (selectAll) {
                    if (locationSelection.printerLocationId.isBlank()) {
                        printerLocationDao.save(
                            PrinterLocationEntity(
                                id = UUID.randomUUID().toString(),
                                printerId = printerData.value.id,
                                locationId = locationSelection.id
                            )
                        )
                    }
                } else {
                    if (locationSelection.printerLocationId.isNotBlank()) {
                        printerLocationDao.delete(locationSelection.printerLocationId)
                    }
                }

            }
        }
    }

    fun savePrinterData() {
        viewModelScope.launch(ioDispatcher) {
            printerDao.update(printerData.value)
            fragmentStateEventBus.currentStateFinished()
        }
    }

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished()
    }

}