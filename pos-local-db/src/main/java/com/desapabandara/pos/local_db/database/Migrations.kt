package com.desapabandara.pos.local_db.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object: Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE PrinterTemplateEntity ADD COLUMN type INTEGER NOT NULL DEFAULT 4")
    }
}
