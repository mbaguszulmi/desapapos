package com.desapabandara.pos.app

import android.app.Application
import android.content.Intent
import android.os.Build
import com.desapabandara.pos.base.manager.AuthManager
import com.desapabandara.pos.service.PosService
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class DesapaApp: Application() {
    @Inject
    lateinit var authManager: AuthManager

    var posService: Intent? = null

    override fun onCreate() {
        super.onCreate()

        initTimber()
        authManager.observeLogin()
    }

    fun startPosServices() {
        if (posService == null) {
            posService = Intent(this, PosService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(posService)
            } else {
                startService(posService)
            }
        }
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}