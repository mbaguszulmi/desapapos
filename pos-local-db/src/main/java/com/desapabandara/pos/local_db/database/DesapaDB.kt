package com.desapabandara.pos.local_db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.desapabandara.pos.local_db.dao.ItemStaffDao
import com.desapabandara.pos.local_db.dao.ItemStatusChangesDao
import com.desapabandara.pos.local_db.dao.LocationDao
import com.desapabandara.pos.local_db.dao.OrderDao
import com.desapabandara.pos.local_db.dao.OrderItemDao
import com.desapabandara.pos.local_db.dao.OrderPaymentDao
import com.desapabandara.pos.local_db.dao.OrderStatusChangesDao
import com.desapabandara.pos.local_db.dao.OrderTableDao
import com.desapabandara.pos.local_db.dao.PaymentMethodDao
import com.desapabandara.pos.local_db.dao.PrinterDao
import com.desapabandara.pos.local_db.dao.PrinterLocationDao
import com.desapabandara.pos.local_db.dao.PrinterTemplateDao
import com.desapabandara.pos.local_db.dao.ProductCategoryDao
import com.desapabandara.pos.local_db.dao.ProductDao
import com.desapabandara.pos.local_db.dao.StaffDao
import com.desapabandara.pos.local_db.dao.StaffLocationAssignmentDao
import com.desapabandara.pos.local_db.dao.StaffPositionDao
import com.desapabandara.pos.local_db.dao.TableDao
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

const val DESAPA_DB = "desapa.db"

@Database(
    entities = [
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
        ItemStaffEntity::class,
        ItemStatusChangesEntity::class,
        OrderStatusChangesEntity::class,
        StaffLocationAssignmentEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class DesapaDB: RoomDatabase() {
    abstract fun getOrderTableDao(): OrderTableDao
    abstract fun getPaymentMethodDao(): PaymentMethodDao
    abstract fun getProductDao(): ProductDao
    abstract fun getStaffDao(): StaffDao
    abstract fun getOrderItemDao(): OrderItemDao
    abstract fun getOrderPaymentDao(): OrderPaymentDao
    abstract fun getPrinterTemplateDao(): PrinterTemplateDao
    abstract fun getTableDao(): TableDao
    abstract fun getProductCategoryDao(): ProductCategoryDao
    abstract fun getStaffPositionDao(): StaffPositionDao
    abstract fun getOrderDao(): OrderDao
    abstract fun getLocationDao(): LocationDao
    abstract fun getPrinterDao(): PrinterDao
    abstract fun getPrinterLocationDao(): PrinterLocationDao
    abstract fun getItemStaffDao(): ItemStaffDao
    abstract fun getItemStatusChangesDao(): ItemStatusChangesDao
    abstract fun getOrderStatusChangesDao(): OrderStatusChangesDao
    abstract fun getStaffLocationAssignmentDao(): StaffLocationAssignmentDao
}