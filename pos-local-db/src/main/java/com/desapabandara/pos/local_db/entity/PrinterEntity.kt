package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PrinterEntity(
    @PrimaryKey
    var id: String,
    var name: String,
    var address: String,
    var paperWidth: Int,
    var isConnected: Boolean,
    var interfaceType: Int = 1,
    override var createdAt: Long = System.currentTimeMillis(),
    override var deletedAt: Long? = null,
    override var updatedAt: Long = System.currentTimeMillis(),
): BaseEntity()