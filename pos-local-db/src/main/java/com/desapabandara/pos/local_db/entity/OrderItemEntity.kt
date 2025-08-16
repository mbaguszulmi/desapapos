package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderItemEntity(
    @PrimaryKey
    var id: String,
    var orderId: String,
    var productId: String,
    var name: String,
    var quantity: Double,
    var isTakeaway: Boolean,
    var priceExcludingTax: Double,
    var tax: Double,
    var isTaxInclusive: Boolean,
    var cost: Double,
    var preparingDuration: Int,
    var status: Int,
    var itemNote: String,
    override var createdAt: Long = System.currentTimeMillis(),
    override var deletedAt: Long? = null,
    override var updatedAt: Long = System.currentTimeMillis(),
): BaseEntity()
