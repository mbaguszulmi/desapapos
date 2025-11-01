package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemStaffEntity(
    @PrimaryKey
    val id: String,
    val itemId: String,
    val staffId: String,
): BaseEntity()
