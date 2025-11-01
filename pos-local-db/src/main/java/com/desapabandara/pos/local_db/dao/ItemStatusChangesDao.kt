package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.ItemStatusChangesEntity

@Dao
abstract class ItemStatusChangesDao: BaseDao<ItemStatusChangesEntity>("ItemStatusChangesEntity") {

    @Query("DELETE FROM ItemStatusChangesEntity WHERE itemId = :itemId")
    abstract fun deleteChangesFromItem(itemId: String)

    @Query("SELECT * FROM ItemStatusChangesEntity WHERE itemId = :itemId ORDER BY createdAt ASC")
    abstract fun getItemStatusChangesByItem(itemId: String): List<ItemStatusChangesEntity>
}