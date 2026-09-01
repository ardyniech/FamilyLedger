package com.example.core.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun householdDao(): HouseholdDao
    abstract fun ledgerAuditDao(): LedgerAuditDao
    abstract fun categoryGroupDao(): CategoryGroupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "household_database"
                )
                .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

