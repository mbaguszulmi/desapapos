package com.desapabandara.pos.base.eventbus

import co.mbznetwork.android.base.di.IoDispatcher
import com.desapabandara.pos.base.model.OrderPaymentEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderPaymentEventBus @Inject constructor(
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())
    private val _orderPaymentEvent = MutableSharedFlow<OrderPaymentEvent>()
    val orderPaymentEvent = _orderPaymentEvent.asSharedFlow()

    fun publish(event: OrderPaymentEvent) {
        scope.launch {
            _orderPaymentEvent.emit(event)
        }
    }
}
