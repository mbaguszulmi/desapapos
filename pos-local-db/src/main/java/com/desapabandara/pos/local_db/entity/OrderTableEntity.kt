package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderTableEntity(
    @PrimaryKey
    var id: String,
    var orderId: String,
    var tableId: String,
    override var createdAt: Long = System.currentTimeMillis(),
    override var deletedAt: Long? = null,
    override var updatedAt: Long = System.currentTimeMillis(),
): BaseEntity()
