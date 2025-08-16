package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.LocationEntity

@Dao
abstract class LocationDao: BaseDao<LocationEntity>("LocationEntity") {
    @Query("SELECT * FROM LocationEntity WHERE id = :id")
    abstract suspend fun getLocation(id: String): LocationEntity?

    @Query("SELECT * FROM LocationEntity")
    abstract suspend fun getLocations(): List<LocationEntity>
}