package com.orbit.blocker.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Base class that spins up an in-memory [OrbitDatabase] for DAO tests. Uses
 * Robolectric so Room can run on the JVM without a device.
 */
@RunWith(RobolectricTestRunner::class)
abstract class DaoTestBase {

    protected lateinit var db: OrbitDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, OrbitDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }
}
