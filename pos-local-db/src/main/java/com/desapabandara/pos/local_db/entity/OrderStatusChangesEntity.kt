package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderStatusChangesEntity(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val status: Int,
    val changedBy: String,
): BaseEntity()
