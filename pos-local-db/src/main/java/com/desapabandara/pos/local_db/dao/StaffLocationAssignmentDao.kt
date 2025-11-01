package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.StaffLocationAssignmentEntity

@Dao
abstract class StaffLocationAssignmentDao: BaseDao<StaffLocationAssignmentEntity>("StaffLocationAssignmentEntity") {

    @Query("SELECT * FROM StaffLocationAssignmentEntity WHERE locationId = :locationId")
    abstract suspend fun getStaffsAssignedByLocation(locationId: String): List<StaffLocationAssignmentEntity>

    @Query("SELECT * FROM StaffLocationAssignmentEntity WHERE staffId = :staffId")
    abstract suspend fun getAssignmentForStaff(staffId: String): StaffLocationAssignmentEntity?
}