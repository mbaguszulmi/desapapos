package com.desapabandara.pos.local_db.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object: Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE PrinterTemplateEntity ADD COLUMN type INTEGER NOT NULL DEFAULT 4")
    }
}

val MIGRATION_2_3 = object: Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE OrderEntity ADD COLUMN waiterId TEXT NOT NULL DEFAULT ''")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ItemStaffEntity` (`id` TEXT NOT NULL PRIMARY KEY, `itemId` TEXT NOT NULL, `staffId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `deletedAt` INTEGER DEFAULT NULL, `updatedAt` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ItemStatusChangesEntity` (`id` TEXT NOT NULL PRIMARY KEY, `itemId` TEXT NOT NULL, `status` INTEGER NOT NULL, `changedBy` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `deletedAt` INTEGER DEFAULT NULL, `updatedAt` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `OrderStatusChangesEntity` (`id` TEXT NOT NULL PRIMARY KEY, `orderId` TEXT NOT NULL, `status` INTEGER NOT NULL, `changedBy` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `deletedAt` INTEGER DEFAULT NULL, `updatedAt` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `StaffLocationAssignmentEntity` (`id` TEXT NOT NULL PRIMARY KEY, `staffId` TEXT NOT NULL, `locationId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `deletedAt` INTEGER DEFAULT NULL, `updatedAt` INTEGER NOT NULL)")
        db.execSQL("UPDATE OrderItemEntity SET status = 4")
        db.execSQL("UPDATE OrderEntity SET waiterId = createdBy")
    }
}

val MIGRATION_3_4 = object: Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE OrderEntity ADD COLUMN isNewCustomer INTEGER NOT NULL DEFAULT 1")
    }
}
