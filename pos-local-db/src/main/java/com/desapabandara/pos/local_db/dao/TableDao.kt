package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.TableEntity

@Dao
abstract class TableDao: BaseDao<TableEntity>("TableEntity") {
    @Query("SELECT * FROM TableEntity WHERE id = :id")
    abstract suspend fun getTable(id: String): TableEntity?

    @Query("SELECT * FROM TableEntity WHERE name = :name")
    abstract suspend fun getTableByName(name: String): TableEntity?
}