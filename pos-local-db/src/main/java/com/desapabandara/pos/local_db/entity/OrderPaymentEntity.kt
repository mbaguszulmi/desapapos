package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderPaymentEntity(
    @PrimaryKey
    var id: String,
    var orderId: String,
    var paymentMethodId: String,
    var amount: Double,
    var referenceNumber: String,
    override var createdAt: Long = System.currentTimeMillis(),
    override var deletedAt: Long? = null,
    override var updatedAt: Long = System.currentTimeMillis(),
): BaseEntity()
