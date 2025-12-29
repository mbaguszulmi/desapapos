package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderEntity(
    @PrimaryKey
    var id: String,
    var storeId: String,
    var invoiceNumber: String,
    var orderNumber: Int,
    var adultMaleCount: Int,
    var adultFemaleCount: Int,
    var childMaleCount: Int,
    var childFemaleCount: Int,
    var orderType: Int,
    var subtotalExcludingTax: Double,
    var subtotalTax: Double,
    var discountExcludingTax: Double,
    var discountTax: Double,
    var surchargeExcludingTax: Double,
    var surchargeTax: Double,
    var totalExcludingTax: Double,
    var totalTax: Double,
    var totalCost: Double,
    var orderStatus: Int,
    var paymentStatus: Int,
    var expectedTotalPreparingDuration: Int,
    var createdBy: String,
    var customerName: String,
    var orderNote: String,
    var waiterId: String,
    var isNewCustomer: Boolean = true,
    var totalAmountTendered: Double = .0,
    var changeRequired: Double = .0,
    override var createdAt: Long = System.currentTimeMillis(),
    override var deletedAt: Long? = null,
    override var updatedAt: Long = System.currentTimeMillis(),
    var synced: Boolean = false
): BaseEntity()
