package com.desapabandara.pos.base.model

import com.desapabandara.pos.local_db.entity.PaymentMethodEntity

sealed class OrderPaymentEvent {
    data class Started(val orderId: String, val amount: Double, val paymentMethod: PaymentMethodEntity) : OrderPaymentEvent()
    data class Settled(val orderId: String, val referenceNumber: String, val amount: Double, val paymentMethod: PaymentMethodEntity) : OrderPaymentEvent()
    data class Failed(val orderId: String, val errorMessage: String) : OrderPaymentEvent()
    data object Cancelled : OrderPaymentEvent()
}
