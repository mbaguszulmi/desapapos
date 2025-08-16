package com.desapabandara.pos.printer.di

import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {
    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context) = try {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    } catch (e: Throwable) {
        Timber.e(e, "Bluetooth adapter is not supported")
        null
    }
}