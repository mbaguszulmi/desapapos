package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import com.desapabandara.pos.local_db.entity.StaffPositionEntity

@Dao
abstract class StaffPositionDao: BaseDao<StaffPositionEntity>("StaffPositionEntity")