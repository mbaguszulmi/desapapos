package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.OrderPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderPaymentDao: BaseDao<OrderPaymentEntity>("OrderPaymentEntity") {
    @Query("SELECT * FROM OrderPaymentEntity WHERE orderId = :orderId")
    abstract fun getOrderPaymentsByOrderId(orderId: String): Flow<List<OrderPaymentEntity>>

    @Query("SELECT * FROM OrderPaymentEntity WHERE orderId = :id")
    abstract suspend fun getOrderPaymentsByOrder(id: String): List<OrderPaymentEntity>
}