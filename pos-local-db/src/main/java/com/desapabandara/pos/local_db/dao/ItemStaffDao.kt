package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.ItemStaffEntity

@Dao
abstract class ItemStaffDao: BaseDao<ItemStaffEntity>("ItemStaffEntity") {

    @Query("DELETE FROM ItemStaffEntity WHERE itemId = :itemId")
    abstract suspend fun deleteStaffsFromItem(itemId: String)

    @Query("SELECT * FROM ItemStaffEntity WHERE itemId = :id")
    abstract suspend fun getStaffsFromItem(id: String): List<ItemStaffEntity>

}
