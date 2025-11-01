package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.StaffPositionEntity

@Dao
abstract class StaffPositionDao: BaseDao<StaffPositionEntity>("StaffPositionEntity") {

    @Query("SELECT * FROM StaffPositionEntity WHERE id = :id")
    abstract suspend fun getPosition(id: String): StaffPositionEntity?
}