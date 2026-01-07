package com.desapabandara.pos.printer.manager

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.util.Printer
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.desapabandara.pos.base.eventbus.OrderPrintEventBus
import com.desapabandara.pos.base.model.ItemStatus
import com.desapabandara.pos.base.model.OrderStatus
import com.desapabandara.pos.base.model.PrinterTemplateType
import com.desapabandara.pos.local_db.dao.LocationDao
import com.desapabandara.pos.local_db.dao.PrinterDao
import com.desapabandara.pos.local_db.dao.PrinterLocationDao
import com.desapabandara.pos.local_db.dao.PrinterTemplateDao
import com.desapabandara.pos.local_db.dao.ProductDao
import com.desapabandara.pos.local_db.entity.PrinterEntity
import com.desapabandara.pos.local_db.entity.PrinterLocationEntity
import com.desapabandara.pos.printer.model.PaperWidth
import com.desapabandara.pos.printer.model.PrintTask
import com.desapabandara.pos.printer.model.PrinterConnection
import com.desapabandara.pos.printer.model.PrinterDevice
import com.desapabandara.pos.printer.model.PrinterInterfaceType
import com.desapabandara.pos.printer.util.MyBluetoothPrintersConnections
import com.desapabandara.pos.printer.util.OrderPrintParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PrinterManager @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val bluetoothAdapter: BluetoothAdapter?,
    private val printerDao: PrinterDao,
    private val printerLocationDao: PrinterLocationDao,
    private val orderPrintEventBus: OrderPrintEventBus,
    private val productDao: ProductDao,
    private val locationDao: LocationDao,
    private val printerTemplateDao: PrinterTemplateDao,
    private val orderPrintParser: OrderPrintParser,
    private val uiStatusEventBus: UIStatusEventBus,
) {
    private var scope: CoroutineScope? = null
    private val mainPrinterDevices = mutableMapOf<String, PrinterDevice>()

    private var lastPrinterObservedDate: Long? = null

    fun start() {
        scope = CoroutineScope(ioDispatcher + SupervisorJob())
        observeSavedDevices()
//        observePrinterConnectionStatus()
        observePrintJob()
    }

    private fun observePrintJob() {
        scope?.launch {
            orderPrintEventBus.printJob.collect {
                val posCounterLocation = locationDao.getLocation("1") ?: return@collect
                val locationsListed = if (it.receiptOnly) {
                    listOf(posCounterLocation)
                } else {
                    (listOf(posCounterLocation) + it.order.orderItems.mapNotNull { item ->
                        productDao.getProductById(item.productId)?.locationId?.let { locationId ->
                            locationDao.getLocation(locationId)
                        }
                    }).distinctBy { location ->
                        location.id
                    }
                }

                val printTasks = locationsListed.flatMap { location ->
                    val devices = getPrinterDevicesByLocationId(location.id)
                    printerTemplateDao.getPrinterTemplate(location.printerTemplateId)?.let { printerTemplate ->
                        if (location.id == "1") {
                            val tableCheckerTemplate = if (!it.receiptOnly) {
                                printerTemplateDao.getPrinterTemplateByType(PrinterTemplateType.TableChecker.id)
                            } else null

                            val checkerOrder = if (tableCheckerTemplate != null) {
                                it.order.copy(
                                    orderItems = it.order.orderItems.filter { item ->
                                        item.status == ItemStatus.New
                                    }
                                ).let { o ->
                                    if (o.orderItems.isEmpty()) null else o
                                }
                            } else {
                                null
                            }

                            val docketTemplate = if (!it.receiptOnly) {
                                printerTemplateDao.getPrinterTemplateByType(PrinterTemplateType.Docket.id)
                            } else null

                            val docketOrder = if (docketTemplate != null) {
                                it.order.copy(
                                    orderItems = it.order.orderItems.filter { item ->
                                        if (item.status != ItemStatus.New) return@filter false

                                        val product = productDao.getProductById(item.productId)
                                        product?.locationId == location.id
                                    }
                                ).let { o ->
                                    if (o.orderItems.isEmpty()) null else o
                                }
                            } else null

                            devices.flatMap { device ->
                                mutableListOf<PrintTask>().apply {
                                    if (it.order.orderStatus == OrderStatus.Completed) {
                                        add(PrintTask(
                                            location,
                                            device,
                                            it.order,
                                            it.reprint,
                                            it.schedulePrint,
                                            printerTemplate
                                        ))
                                    }

                                    if (checkerOrder != null && tableCheckerTemplate != null) {
                                        add(PrintTask(
                                            location,
                                            device,
                                            checkerOrder,
                                            it.reprint,
                                            it.schedulePrint,
                                            tableCheckerTemplate
                                        ))
                                    }

                                    if (docketOrder != null && docketTemplate != null) {
                                        add(PrintTask(
                                            location,
                                            device,
                                            docketOrder,
                                            it.reprint,
                                            it.schedulePrint,
                                            docketTemplate
                                        ))
                                    }
                                }
                            }
                        } else {
                            val order = it.order.copy(
                                orderItems = it.order.orderItems.filter { item ->
                                    if (item.status != ItemStatus.New) return@filter false

                                    val product = productDao.getProductById(item.productId)
                                    product?.locationId == location.id
                                }
                            )

                            if (order.orderItems.isEmpty()) return@flatMap emptyList()

                            devices.map { device ->
                                PrintTask(
                                    location,
                                    device,
                                    order,
                                    it.reprint,
                                    it.schedulePrint,
                                    printerTemplate
                                )
                            }
                        }
                    } ?: emptyList()
                }

                var isSucceeded = true

                printTasks.forEach { task ->
                    val parsedText = orderPrintParser.parseFromTask(task)

                    try {
                        task.printerDevice.connect()
                        if (!printFormatted(
                                parsedText,
                                task.printerDevice.printerData.id,
                                if (task.printerTemplate.type != PrinterTemplateType.Docket.id) {
                                    0f
                                } else {
                                    15f
                                },
                                task.printerTemplate.type == PrinterTemplateType.Receipt.id
                            )) {
                            isSucceeded = false
                        }
                        task.printerDevice.disconnect()
                    } catch (e: Throwable) {
                        Timber.e(e, "Printing failed for task: $task")
                        uiStatusEventBus.setUiStatus(
                            UiStatus.ShowError(
                                UiMessage.StringMessage("Printing failed for printer ${task.printerDevice.printerData.name}: ${e.message}")
                            )
                        )
                        isSucceeded = false
                    }
                }
            }
        }
    }

    private fun getPrinterDevicesByLocationId(locationId: String): List<PrinterDevice> {
        return mainPrinterDevices.values.filter {
            it.printerData.isConnected && it.locations.any { l -> l.id == locationId }
        }
    }

    @SuppressLint("MissingPermission")
    private fun observePrinterConnectionStatus() {
        scope?.launch {
            while (true) {
                mainPrinterDevices.values.forEach {
                    val isDeviceConnected = it.deviceConnection.let { conn ->
                        if (conn is BluetoothConnection) {
                            bluetoothAdapter?.isEnabled == true && conn.isConnected
                        } else {
                            conn.isConnected
                        }
                    }

                    if (isDeviceConnected != it.printerData.isConnected) {
                        printerDao.update(it.printerData.apply {
                            isConnected = isDeviceConnected
                        })
                    }
                }

                delay(10000)
            }
        }
    }

    private fun observeSavedDevices() {
        scope?.launch {
            while(true) {
                val lastObservedDate = lastPrinterObservedDate
                val printers = if (lastObservedDate == null) {
                    printerDao.getAll().filterNot { it.isEmpty() }.first()
                } else {
                    printerDao.getPrinterAfterDate(lastObservedDate).filterNot { it.isEmpty() }.first()
                }

                val updatedLastObservedDate = System.currentTimeMillis()
                lastPrinterObservedDate = updatedLastObservedDate

                for (printer in printers) {
                    if (printer.isConnected) {
                        mainPrinterDevices[printer.id]?.let { device ->
                            val printerLocations = printerLocationDao.getPrinterLocationFromPrinter(printer.id)

                            mainPrinterDevices[printer.id] = PrinterDevice(
                                device.deviceConnection,
                                device.printer,
                                printer,
                                printerLocations
                            )
                        } ?: run {
                            if (printer.interfaceType == PrinterInterfaceType.Bluetooth.id) {
                                val connectionResult = connectBluetoothPrinter(printer)
                                if (connectionResult != null) {
                                    val printerLocations = printerLocationDao.getPrinterLocationFromPrinter(printer.id)
                                    val printerDevice = PrinterDevice(
                                        connectionResult.first,
                                        connectionResult.second,
                                        printer,
                                        printerLocations
                                    )
                                    printerDevice.printer.disconnect()

                                    mainPrinterDevices[printer.id] = printerDevice
                                } else {
                                    printerDao.update(printer.apply {
                                        isConnected = false
                                        updatedAt = updatedLastObservedDate
                                    }, false)
                                }
                            }

                            // TODO: other interfaces haven't implemented yet
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getBluetoothDeviceList(onListUpdated: (List<PrinterConnection>) -> Unit) = scope?.launch {
        val bluetoothConnections = MyBluetoothPrintersConnections()
        while (true) {
            val bluetoothList = bluetoothConnections.list
            onListUpdated(bluetoothList.toList().map {
                PrinterConnection(it, it.device.address, it.device.name)
            })
            delay(10000)
        }
    }

    fun scanBluetoothDevices() = callbackFlow {
        val job = getBluetoothDeviceList {
            trySend(it)
        }

        awaitClose {
            job?.cancel()
        }
    }

    private fun connectBluetoothPrinter(printer: PrinterEntity): Pair<DeviceConnection, EscPosPrinterCommands>? {
        return bluetoothAdapter?.let {
            try {
                val deviceConnection = BluetoothConnection(it.getRemoteDevice(printer.address))
                deviceConnection to EscPosPrinterCommands(deviceConnection).connect()
            } catch (e: Throwable) {
                Timber.e(e, "Error connecting printer $printer")
                null
            }
        }
    }

    fun reconnectBluetoothPrinter(printerId: String) {
        scope?.launch {
            val printer = printerDao.getPrinter(printerId) ?: return@launch

            if (printer.isConnected) {
                mainPrinterDevices.remove(printer.id)
            }

            printerDao.update(printer.apply {
                isConnected = !isConnected
            })

        }
    }

    suspend fun addBluetoothPrinter(printer: PrinterConnection, name: String, paperWidth: PaperWidth, locations: List<String>): PrinterEntity {
        val printerData = PrinterEntity(
            UUID.randomUUID().toString(),
            name.ifBlank { printer.name },
            printer.address,
            paperWidth.width,
            true,
            1,
        )

        printerDao.save(printerData)

        for (locationId in locations) {
            printerLocationDao.save(PrinterLocationEntity(
                UUID.randomUUID().toString(),
                printerData.id,
                locationId
            ))
        }

        return printerData
    }

    fun deletePrinter(id: String) {
        scope?.launch {
            printerDao.deletePrinter(id)

            mainPrinterDevices[id]?.printer?.disconnect()
            mainPrinterDevices.remove(id)
        }
    }

    fun printTestPage(printerId: String) {
        scope?.launch {
            val printerDevice = mainPrinterDevices[printerId] ?: return@launch
            try {
                printerDevice.connect()
                val text = "[C]TEST PAGE\n" +
                        "[L]Name: ${printerDevice.printerData.name}\n" +
                        "[L]Address: ${printerDevice.printerData.address}\n"

                printFormatted(text, printerId)
                printerDevice.disconnect()
            } catch (e: Throwable) {
                Timber.e(e, "Failed to print test page")
                uiStatusEventBus.setUiStatus(
                    UiStatus.ShowError(
                        UiMessage.StringMessage("Failed to print test page: ${e.message}")
                    )
                )
                return@launch
            }
        }
    }

    private fun printFormatted(text: String, printerId: String, feedMM: Float = 0f, openCashBox: Boolean = false): Boolean {
        return try {
            val printerDevice = mainPrinterDevices[printerId] ?: return false
            val paperWidth = PaperWidth.fromWidth(printerDevice.printerData.paperWidth) ?: return false
            val escPosPrinter = EscPosPrinter(
                printerDevice.printer,
                203,
                paperWidth.width.toFloat(),
                paperWidth.characters
            )

            escPosPrinter.printFormattedTextAndCut(text, feedMM)
            if (openCashBox) {
                printerDevice.printer.openCashBox()
            }

            true
        } catch (e: Throwable) {
            Timber.e(e, "Printing failed!")
            false
        }
    }

    private suspend fun PrinterDevice.connect() {
        try {
            printer.connect()
        } catch (e: Throwable) {
            Timber.e(e, "Error connecting printer ${printerData}: ${e.message}")
            mainPrinterDevices.remove(printerData.id)
            printerDao.update(printerData.apply {
                isConnected = !isConnected
            })
            throw e
        }
    }

    private fun PrinterDevice.disconnect() {
        printer.disconnect()
    }

    fun stop() {
        scope?.cancel()
        scope = null
        lastPrinterObservedDate = null
        mainPrinterDevices.clear()
    }
}