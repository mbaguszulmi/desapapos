package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StaffEntity(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var pin: String = "",
    var isActive: Boolean = true,
    var userId: String = "",
    var multiShift: Boolean = false,
    var phoneNumber: String = "",
    var positionId: String = "",
    var avatarUrl: String = "",
    override var createdAt: Long = System.currentTimeMillis(),
    override var deletedAt: Long? = null,
    override var updatedAt: Long = System.currentTimeMillis(),
): BaseEntity()
