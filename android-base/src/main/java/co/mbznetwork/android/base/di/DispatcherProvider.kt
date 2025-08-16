package co.mbznetwork.android.base.di

import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    val main get() = Dispatchers.Main
    val io get() = Dispatchers.IO
    val default get() = Dispatchers.Default
}

class DefaultDispatcherProvider: DispatcherProvider