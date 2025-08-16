package com.desapabandara.pos.base.eventbus

import com.desapabandara.pos.base.model.OrderPrintJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderPrintEventBus @Inject constructor() {
    private val _printJob = MutableSharedFlow<OrderPrintJob>()
    val printJob = _printJob.asSharedFlow()

    suspend fun publishJob(job: OrderPrintJob) {
        _printJob.emit(job)
    }
}
