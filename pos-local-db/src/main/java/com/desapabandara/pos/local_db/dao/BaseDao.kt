package com.desapabandara.pos.local_db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import com.desapabandara.pos.local_db.entity.BaseEntity
import com.desapabandara.pos.local_db.entity.ItemStaffEntity
import com.desapabandara.pos.local_db.entity.ItemStatusChangesEntity
import com.desapabandara.pos.local_db.entity.LocationEntity
import com.desapabandara.pos.local_db.entity.OrderEntity
import com.desapabandara.pos.local_db.entity.OrderItemEntity
import com.desapabandara.pos.local_db.entity.OrderPaymentEntity
import com.desapabandara.pos.local_db.entity.OrderStatusChangesEntity
import com.desapabandara.pos.local_db.entity.OrderTableEntity
import com.desapabandara.pos.local_db.entity.PaymentMethodEntity
import com.desapabandara.pos.local_db.entity.PrinterEntity
import com.desapabandara.pos.local_db.entity.PrinterLocationEntity
import com.desapabandara.pos.local_db.entity.PrinterTemplateEntity
import com.desapabandara.pos.local_db.entity.ProductCategoryEntity
import com.desapabandara.pos.local_db.entity.ProductEntity
import com.desapabandara.pos.local_db.entity.StaffEntity
import com.desapabandara.pos.local_db.entity.StaffLocationAssignmentEntity
import com.desapabandara.pos.local_db.entity.StaffPositionEntity
import com.desapabandara.pos.local_db.entity.TableEntity
import kotlinx.coroutines.flow.Flow

abstract class BaseDao<T: BaseEntity>(
    private val tableName: String
) {

    @RawQuery(observedEntities = [
        LocationEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        OrderPaymentEntity::class,
        OrderTableEntity::class,
        PaymentMethodEntity::class,
        PrinterTemplateEntity::class,
        ProductCategoryEntity::class,
        ProductEntity::class,
        StaffEntity::class,
        StaffPositionEntity::class,
        TableEntity::class,
        PrinterEntity::class,
        PrinterLocationEntity::class,
        PrinterLocationEntity::class,
        ItemStaffEntity::class,
        ItemStatusChangesEntity::class,
        OrderStatusChangesEntity::class,
        StaffLocationAssignmentEntity::class
    ])
    protected abstract fun getAllRaw(query: SimpleSQLiteQuery): Flow<List<T>>

    fun getAll(
        orderColumn: BaseOrderColumn = BaseOrderColumn.None,
        orderDirection: OrderDirection = OrderDirection.ASC
    ): Flow<List<T>> {
        var query = "SELECT * FROM $tableName"
        if (orderColumn != BaseOrderColumn.None) {
            query = "$query ORDER BY ${orderColumn.field} ${orderDirection.dir}"
        }

        return getAllRaw(SimpleSQLiteQuery(query))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMany(data: List<T>)

    @Upsert
    abstract suspend fun save(data: T)

    @Delete
    abstract suspend fun delete(data: T)

    @Update
    protected abstract suspend fun updateValue(data: T)

    suspend fun update(data: T, updateTimestamp: Boolean = true) {
        if (updateTimestamp) {
            data.updatedAt = System.currentTimeMillis()
        }

        updateValue(data)
    }
}

enum class BaseOrderColumn(
    val field: String
) {
    None(""),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}

enum class OrderDirection(
    val dir: String
) {
    ASC("ASC"),
    DESC("DESC")
}
