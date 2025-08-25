package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.StaffEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StaffDao: BaseDao<StaffEntity>("StaffEntity") {
    @Query("SELECT * FROM StaffEntity WHERE id = :id")
    abstract fun getStaffById(id: String): Flow<StaffEntity?>

    @Query("SELECT * FROM StaffEntity WHERE id = :id")
    abstract suspend fun getStaffByIdSingle(id: String): StaffEntity?

    @Query("SELECT * FROM StaffEntity WHERE pin = :pin")
    abstract suspend fun getStaffByPin(pin: String): StaffEntity?
}