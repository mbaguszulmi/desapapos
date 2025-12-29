package com.desapabandara.pos.base.manager

import co.mbznetwork.android.base.di.IoDispatcher
import com.desapabandara.pos.base.eventbus.OrderPaymentEventBus
import com.desapabandara.pos.base.model.OrderPaymentEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderPaymentManager @Inject constructor(
    private val orderPaymentEventBus: OrderPaymentEventBus,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private var scope: CoroutineScope? = null

    fun start() {
        scope?.cancel()
        scope = CoroutineScope(ioDispatcher + SupervisorJob())

        monitorPayments()
    }

    private fun monitorPayments() {
        scope?.launch {
            orderPaymentEventBus.orderPaymentEvent.filterIsInstance<OrderPaymentEvent.Started>().collect {
                orderPaymentEventBus.publish(OrderPaymentEvent.Settled(
                    it.orderId,
                    UUID.randomUUID().toString(),
                    it.amount,
                    it.paymentMethod
                ))
            }
        }
    }

    fun stop() {
        scope?.cancel()
        scope = null
    }
}