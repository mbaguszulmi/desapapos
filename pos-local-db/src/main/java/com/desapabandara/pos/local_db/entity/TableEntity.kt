package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TableEntity(
    @PrimaryKey
    var id: String,
    var name: String,
    var isActive: Boolean,
    var tableCapacity: Int,
    override var createdAt: Long = System.currentTimeMillis(),
    override var deletedAt: Long? = null,
    override var updatedAt: Long = System.currentTimeMillis(),
): BaseEntity()
