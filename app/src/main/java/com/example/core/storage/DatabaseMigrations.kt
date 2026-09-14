package com.example.core.storage

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = LegacyMigrations.MIGRATION_1_2
    val MIGRATION_2_3 = LegacyMigrations.MIGRATION_2_3
    val MIGRATION_3_4 = LegacyMigrations.MIGRATION_3_4
    val MIGRATION_4_5 = LegacyMigrations.MIGRATION_4_5
    val MIGRATION_5_6 = LegacyMigrations.MIGRATION_5_6

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            try { db.execSQL("ALTER TABLE transactions ADD COLUMN goalId TEXT DEFAULT NULL") } catch (_: Exception) {}
        }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS household_expenses (" +
                    "id TEXT PRIMARY KEY NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "amount INTEGER NOT NULL, " +
                    "categoryId TEXT NOT NULL, " +
                    "categoryName TEXT NOT NULL, " +
                    "walletId TEXT NOT NULL, " +
                    "walletName TEXT NOT NULL, " +
                    "memberId TEXT NOT NULL, " +
                    "memberName TEXT NOT NULL, " +
                    "expenseDate INTEGER NOT NULL, " +
                    "isNeed INTEGER NOT NULL DEFAULT 1, " +
                    "receiptPath TEXT NOT NULL DEFAULT '', " +
                    "notes TEXT NOT NULL DEFAULT '', " +
                    "syncStatus INTEGER NOT NULL DEFAULT 0, " +
                    "createdAt INTEGER NOT NULL DEFAULT 0, " +
                    "isDeleted INTEGER NOT NULL DEFAULT 0" +
                ")"
            )
        }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val sqls = listOf(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_households_pairCode ON households(pairCode)",
                "CREATE INDEX IF NOT EXISTS index_members_householdId ON members(householdId)",
                "CREATE INDEX IF NOT EXISTS index_members_syncStatus ON members(syncStatus)",
                "CREATE INDEX IF NOT EXISTS index_wallet_accounts_memberId ON wallet_accounts(memberId)",
                "CREATE INDEX IF NOT EXISTS index_wallet_accounts_syncStatus ON wallet_accounts(syncStatus)",
                "CREATE INDEX IF NOT EXISTS index_category_groups_syncStatus ON category_groups(syncStatus)",
                "CREATE INDEX IF NOT EXISTS index_categories_groupId ON categories(groupId)",
                "CREATE INDEX IF NOT EXISTS index_categories_parentId ON categories(parentId)",
                "CREATE INDEX IF NOT EXISTS index_categories_syncStatus ON categories(syncStatus)",
                "CREATE INDEX IF NOT EXISTS index_transactions_walletId ON transactions(walletId)",
                "CREATE INDEX IF NOT EXISTS index_transactions_categoryId ON transactions(categoryId)",
                "CREATE INDEX IF NOT EXISTS index_transactions_memberId ON transactions(memberId)",
                "CREATE INDEX IF NOT EXISTS index_transactions_timestamp ON transactions(timestamp)",
                "CREATE INDEX IF NOT EXISTS index_transactions_syncStatus ON transactions(syncStatus)",
                "CREATE INDEX IF NOT EXISTS index_transactions_goalId ON transactions(goalId)",
                "CREATE INDEX IF NOT EXISTS index_household_expenses_walletId ON household_expenses(walletId)",
                "CREATE INDEX IF NOT EXISTS index_household_expenses_categoryId ON household_expenses(categoryId)",
                "CREATE INDEX IF NOT EXISTS index_household_expenses_memberId ON household_expenses(memberId)",
                "CREATE INDEX IF NOT EXISTS index_household_expenses_expenseDate ON household_expenses(expenseDate)",
                "CREATE INDEX IF NOT EXISTS index_household_expenses_syncStatus ON household_expenses(syncStatus)",
                "CREATE INDEX IF NOT EXISTS index_ledger_events_householdId ON ledger_events(householdId)",
                "CREATE INDEX IF NOT EXISTS index_ledger_events_entityId ON ledger_events(entityId)",
                "CREATE INDEX IF NOT EXISTS index_ledger_events_actorId ON ledger_events(actorId)",
                "CREATE INDEX IF NOT EXISTS index_ledger_events_syncStatus ON ledger_events(syncStatus)",
                "CREATE INDEX IF NOT EXISTS index_ledger_events_createdAt ON ledger_events(createdAt)",
                "CREATE INDEX IF NOT EXISTS index_internal_transfers_sourceWalletId ON internal_transfers(sourceWalletId)",
                "CREATE INDEX IF NOT EXISTS index_internal_transfers_destinationWalletId ON internal_transfers(destinationWalletId)",
                "CREATE INDEX IF NOT EXISTS index_internal_transfers_syncStatus ON internal_transfers(syncStatus)",
                "CREATE INDEX IF NOT EXISTS index_internal_transfers_timestamp ON internal_transfers(timestamp)"
            )
            sqls.forEach { db.execSQL(it) }
        }
    }

    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
        MIGRATION_6_7,
        MIGRATION_7_8,
        MIGRATION_8_9
    )
}
