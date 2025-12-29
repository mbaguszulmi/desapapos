package com.desapabandara.pos.service

import android.content.Intent
import android.os.IBinder
import co.mbznetwork.android.base.NetworkStatusManager
import co.mbznetwork.android.base.service.AppService
import com.desapabandara.pos.base.eventbus.StoreLoginEventBus
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.base.manager.OrderPaymentManager
import com.desapabandara.pos.printer.manager.PrinterManager
import com.desapabandara.pos.sync.manager.OrderSyncManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PosService: AppService() {

    @Inject
    lateinit var storeLoginEventBus: StoreLoginEventBus

    @Inject
    lateinit var orderSyncManager: OrderSyncManager

    @Inject
    lateinit var orderManager: OrderManager

    @Inject
    lateinit var orderPaymentManager: OrderPaymentManager

    @Inject
    lateinit var networkStatusManager: NetworkStatusManager

    @Inject
    lateinit var printerManager: PrinterManager

    override fun channelId(): String = javaClass.simpleName

    override fun notificationId(): Int = 1

    override fun serviceName(): String = javaClass.simpleName

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startMonitoringNetworkStatus()
        monitorAuthenticationStatus()
    }

    private fun startMonitoringNetworkStatus() {
        networkStatusManager.start()
    }

    private fun stopMonitoringNetworkStatus() {
        networkStatusManager.stop()
    }

    private fun monitorAuthenticationStatus() {
        launch {
            storeLoginEventBus.currentStore.collect {
                if (it != null) {
                    Timber.d("Starting Pos Services")
                    startServices()
                } else {
                    Timber.d("Stopping Pos Services")
                    stopServices()
                }
            }
        }
    }

    private fun startServices() {
        orderManager.start()
        orderPaymentManager.start()
        orderSyncManager.start()
        printerManager.start()
    }

    private fun stopServices() {
        orderManager.stop()
        orderPaymentManager.stop()
        orderSyncManager.stop()
        printerManager.stop()
    }

    override fun onDestroy() {
        stopMonitoringNetworkStatus()
        super.onDestroy()
    }
}