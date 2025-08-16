package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.OrderTableEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderTableDao: BaseDao<OrderTableEntity>("OrderTableEntity") {
    @Query("SELECT * FROM OrderTableEntity WHERE orderId = :orderId")
    abstract fun getOrderTableByOrderId(orderId: String): Flow<OrderTableEntity?>

    @Query("SELECT * FROM OrderTableEntity WHERE orderId = :orderId")
    abstract fun getOrderTableByOrder(orderId: String): OrderTableEntity?

    @Query("DELETE FROM OrderTableEntity WHERE orderId = :id")
    abstract suspend fun deleteTableFromOrder(id: String)
}