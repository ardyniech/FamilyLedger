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
        TransferEventEntity::class,
        HouseholdExpense::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun householdDao(): HouseholdDao
    abstract fun ledgerAuditDao(): LedgerAuditDao
    abstract fun categoryGroupDao(): CategoryGroupDao
    abstract fun householdExpenseDao(): HouseholdExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "household_database"
                ).addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)

                if (!DatabasePassphraseHelper.isRobolectricOrTest()) {
                    val passphrase = DatabasePassphraseHelper.getOrCreatePassphrase(context.applicationContext)
                    DatabasePassphraseHelper.prepareDatabase(context.applicationContext, "household_database", passphrase)
                    val factory = net.sqlcipher.database.SupportFactory(passphrase)
                    builder.openHelperFactory(factory)
                }

                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }
    }
}

