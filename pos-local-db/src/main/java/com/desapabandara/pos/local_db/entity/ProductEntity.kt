package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey
    var id: String,
    var name: String,
    var productCode: String,
    var description: String,
    var priceExcludingTax: Double,
    var tax: Double,
    var isTaxInclusive: Boolean,
    var cost: Double,
    var stock: Double = .0,
    var preparingDuration: Int = 0,
    var imageUrl: String = "",
    var categoryId: String = "",
    var locationId: String = "",
    var isActive: Boolean = true,
    var isIngredientOnly: Boolean = false,
    override var createdAt: Long = System.currentTimeMillis(),
    override var deletedAt: Long? = null,
    override var updatedAt: Long = System.currentTimeMillis(),
): BaseEntity()
