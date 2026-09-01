package com.example.core.storage

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS ledger_events (id TEXT PRIMARY KEY NOT NULL, householdId TEXT NOT NULL, entityId TEXT NOT NULL, actorId TEXT NOT NULL, deviceId TEXT NOT NULL, eventType TEXT NOT NULL, amount INTEGER NOT NULL, reason TEXT NOT NULL DEFAULT '', referenceEntityId TEXT NOT NULL DEFAULT '', currency TEXT NOT NULL DEFAULT 'IDR', timestamp INTEGER NOT NULL, syncState TEXT NOT NULL DEFAULT 'PENDING')")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS transfer_events (eventId TEXT PRIMARY KEY NOT NULL, sourceWalletId TEXT NOT NULL, targetWalletId TEXT NOT NULL, amount INTEGER NOT NULL, note TEXT NOT NULL, timestamp INTEGER NOT NULL, memberId TEXT NOT NULL, syncStatus INTEGER NOT NULL DEFAULT 0)")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            try {
                db.execSQL("ALTER TABLE categories ADD COLUMN groupId TEXT DEFAULT NULL")
            } catch (_: Exception) {}
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS category_groups (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, colorHex TEXT NOT NULL DEFAULT '#3B82F6', iconName TEXT NOT NULL DEFAULT '📁', description TEXT NOT NULL DEFAULT '', syncStatus INTEGER NOT NULL DEFAULT 0, updatedAt INTEGER NOT NULL DEFAULT 0, isDeleted INTEGER NOT NULL DEFAULT 0)")
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            try {
                db.execSQL("ALTER TABLE ledger_events ADD COLUMN reason TEXT NOT NULL DEFAULT ''")
            } catch (_: Exception) {}
            try {
                db.execSQL("ALTER TABLE ledger_events ADD COLUMN referenceEntityId TEXT NOT NULL DEFAULT ''")
            } catch (_: Exception) {}

            db.execSQL("CREATE TABLE IF NOT EXISTS transactions_new (id TEXT PRIMARY KEY NOT NULL, walletId TEXT NOT NULL, memberId TEXT NOT NULL, categoryId TEXT NOT NULL, amount INTEGER NOT NULL, note TEXT NOT NULL, timestamp INTEGER NOT NULL, syncStatus INTEGER NOT NULL DEFAULT 0, updatedAt INTEGER NOT NULL DEFAULT 0, isDeleted INTEGER NOT NULL DEFAULT 0)")
            db.execSQL("INSERT INTO transactions_new SELECT id, walletId, memberId, categoryId, CAST(amount AS INTEGER), note, timestamp, syncStatus, updatedAt, isDeleted FROM transactions")
            db.execSQL("DROP TABLE transactions")
            db.execSQL("ALTER TABLE transactions_new RENAME TO transactions")

            db.execSQL("CREATE TABLE IF NOT EXISTS wallet_accounts_new (id TEXT PRIMARY KEY NOT NULL, memberId TEXT NOT NULL, type TEXT NOT NULL, name TEXT NOT NULL, balance INTEGER NOT NULL, monthlyTransferCap INTEGER NOT NULL DEFAULT 0, syncStatus INTEGER NOT NULL DEFAULT 0, updatedAt INTEGER NOT NULL DEFAULT 0, isDeleted INTEGER NOT NULL DEFAULT 0)")
            db.execSQL("INSERT INTO wallet_accounts_new SELECT id, memberId, type, name, CAST(balance AS INTEGER), CAST(monthlyTransferCap AS INTEGER), syncStatus, updatedAt, isDeleted FROM wallet_accounts")
            db.execSQL("DROP TABLE wallet_accounts")
            db.execSQL("ALTER TABLE wallet_accounts_new RENAME TO wallet_accounts")

            db.execSQL("CREATE TABLE IF NOT EXISTS categories_new (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, type TEXT NOT NULL, iconName TEXT NOT NULL DEFAULT '', parentId TEXT, groupId TEXT, isSavings INTEGER NOT NULL DEFAULT 0, syncStatus INTEGER NOT NULL DEFAULT 0, updatedAt INTEGER NOT NULL DEFAULT 0, isDeleted INTEGER NOT NULL DEFAULT 0, budgetLimit INTEGER NOT NULL DEFAULT 0)")
            db.execSQL("INSERT INTO categories_new SELECT id, name, type, iconName, parentId, groupId, isSavings, syncStatus, updatedAt, isDeleted, CAST(budgetLimit AS INTEGER) FROM categories")
            db.execSQL("DROP TABLE categories")
            db.execSQL("ALTER TABLE categories_new RENAME TO categories")
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            try {
                db.execSQL("ALTER TABLE transactions ADD COLUMN goalId TEXT DEFAULT NULL")
            } catch (_: Exception) {}
        }
    }

    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
        MIGRATION_6_7
    )
}
