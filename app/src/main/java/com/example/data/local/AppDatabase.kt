package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.core.storage.DatabasePassphraseHelper

@Database(entities = [Expense::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_database"
                ).fallbackToDestructiveMigration(true)

                if (!DatabasePassphraseHelper.isRobolectricOrTest()) {
                    val passphrase = DatabasePassphraseHelper.getOrCreatePassphrase(context.applicationContext)
                    DatabasePassphraseHelper.prepareDatabase(context.applicationContext, "expense_database", passphrase)
                    val factory = net.sqlcipher.database.SupportFactory(passphrase)
                    builder.openHelperFactory(factory)
                }

                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): AppDatabase = getDatabase(context)
    }
}
