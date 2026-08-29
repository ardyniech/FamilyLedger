package com.example.core.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.shared.models.*

@Database(
    entities = [
        Household::class,
        Member::class,
        WalletAccount::class,
        Category::class,
        CategoryGroup::class,
        Transaction::class,
        LedgerEvent::class,
        TransferEventEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun householdDao(): HouseholdDao
    abstract fun ledgerAuditDao(): LedgerAuditDao
    abstract fun categoryGroupDao(): CategoryGroupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "household_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

