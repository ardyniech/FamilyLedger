package com.example.core.storage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DatabaseMigrationTest {

    @Test
    fun testAllMigrationsArray_hasContinuousMigrationChain() {
        val migrations = DatabaseMigrations.ALL_MIGRATIONS
        assertEquals(6, migrations.size)
        assertEquals(1, migrations[0].startVersion)
        assertEquals(2, migrations[0].endVersion)
        assertEquals(2, migrations[1].startVersion)
        assertEquals(3, migrations[1].endVersion)
        assertEquals(3, migrations[2].startVersion)
        assertEquals(4, migrations[2].endVersion)
        assertEquals(4, migrations[3].startVersion)
        assertEquals(5, migrations[3].endVersion)
        assertEquals(5, migrations[4].startVersion)
        assertEquals(6, migrations[4].endVersion)
        assertEquals(6, migrations[5].startVersion)
        assertEquals(7, migrations[5].endVersion)
    }

    @Test
    fun testDatabaseCreation_withMigrations_initializesCorrectly() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
            .build()

        assertNotNull(db)
        assertNotNull(db.householdDao())
        assertNotNull(db.categoryGroupDao())
        assertNotNull(db.ledgerAuditDao())
        db.close()
    }
}
