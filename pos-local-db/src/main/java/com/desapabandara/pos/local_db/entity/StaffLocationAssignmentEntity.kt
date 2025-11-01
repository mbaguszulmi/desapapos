package com.desapabandara.pos.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StaffLocationAssignmentEntity(
    @PrimaryKey
    var id: String,
    var staffId: String,
    var locationId: String
): BaseEntity()
