package com.desapabandara.pos.app

import android.app.Application
import com.desapabandara.pos.base.manager.AuthManager
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.printer.manager.PrinterManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class DesapaApp: Application() {
    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var orderManager: OrderManager

    @Inject
    lateinit var printerManager: PrinterManager

    override fun onCreate() {
        super.onCreate()

        initTimber()
        authManager.observeLogin()
        orderManager.start()
        printerManager.initiateConnections()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}