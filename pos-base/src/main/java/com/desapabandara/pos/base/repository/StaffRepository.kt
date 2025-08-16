package com.desapabandara.pos.base.repository

import com.desapabandara.pos.local_db.dao.StaffDao
import com.desapabandara.pos.local_db.dao.StaffPositionDao
import com.desapabandara.pos.local_db.entity.StaffEntity
import com.desapabandara.pos.local_db.entity.StaffPositionEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor(
    private val staffDao: StaffDao,
    private val staffPositionDao: StaffPositionDao
) {
    suspend fun saveStaffs(staffs: List<StaffEntity>) {
        staffDao.insertMany(staffs)
    }

    suspend fun saveStaffPositions(staffPositions: List<StaffPositionEntity>) {
        staffPositionDao.insertMany(staffPositions)
    }
}