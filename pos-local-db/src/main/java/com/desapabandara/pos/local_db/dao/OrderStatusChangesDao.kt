package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.OrderStatusChangesEntity

@Dao
abstract class OrderStatusChangesDao: BaseDao<OrderStatusChangesEntity>("OrderStatusChangesEntity") {
    @Query("DELETE FROM OrderStatusChangesEntity WHERE orderId = :orderId")
    abstract fun deleteChangesFromOrder(orderId: String)

    @Query("SELECT * FROM OrderStatusChangesEntity WHERE orderId = :orderId ORDER BY createdAt ASC")
    abstract suspend fun getOrderStatusChangesByOrder(orderId: String): List<OrderStatusChangesEntity>
}