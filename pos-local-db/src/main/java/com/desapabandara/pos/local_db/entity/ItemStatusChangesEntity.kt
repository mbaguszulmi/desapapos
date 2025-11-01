package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemStatusChangesEntity(
    @PrimaryKey
    val id: String,
    val itemId: String,
    val status: Int,
    val changedBy: String,
): BaseEntity()
