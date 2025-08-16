package com.desapabandara.pos.local_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.desapabandara.pos.local_db.entity.PaymentMethodEntity

@Dao
abstract class PaymentMethodDao: BaseDao<PaymentMethodEntity>("PaymentMethodEntity") {
    @Query("SELECT * FROM PaymentMethodEntity")
    abstract suspend fun getFirstPaymentMethod(): PaymentMethodEntity?
}