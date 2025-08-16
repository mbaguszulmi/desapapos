package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderItemDao: BaseDao<OrderItemEntity>("OrderItemEntity") {
    @Query("SELECT * FROM OrderItemEntity WHERE orderId = :orderId")
    abstract fun getOrderItemsByOrderId(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM OrderItemEntity WHERE orderId = :orderId")
    abstract fun getOrderItemsByOrder(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM OrderItemEntity WHERE id IN (:ids)")
    abstract fun getOrderItems(ids: List<String>): List<OrderItemEntity>

    @Query("SELECT * FROM OrderItemEntity WHERE orderId = :orderId AND productId = :productId AND status = :status")
    abstract fun getOrderItemByProductIdAndOrderId(orderId: String, productId: String, status: Int): OrderItemEntity?

    @Query("SELECT * FROM OrderItemEntity WHERE id = :id")
    abstract fun getOrderItemById(id: String): OrderItemEntity?

    @Query("DELETE FROM OrderItemEntity WHERE orderId = :orderId")
    abstract suspend fun deleteItemsInOrder(orderId: String)


}