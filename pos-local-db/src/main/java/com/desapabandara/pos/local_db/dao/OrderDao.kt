package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderDao: BaseDao<OrderEntity>("OrderEntity") {
    @Query("SELECT * FROM OrderEntity WHERE orderStatus = 1")
    abstract fun getActiveOrder(): Flow<OrderEntity?>

    @Query("SELECT * FROM OrderEntity WHERE id = :id")
    abstract suspend fun getOrderById(id: String): OrderEntity?

    @Query("DELETE FROM OrderEntity WHERE id = :id")
    abstract suspend fun deleteOrder(id: String)

    @Query("SELECT * FROM OrderEntity WHERE orderStatus = 2 ORDER BY createdAt DESC")
    abstract fun getHeldOrders(): Flow<List<OrderEntity>>
}