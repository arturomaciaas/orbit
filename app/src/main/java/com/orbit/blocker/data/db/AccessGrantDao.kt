package com.orbit.blocker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.orbit.blocker.data.model.AccessGrant

@Dao
interface AccessGrantDao {

    @Insert
    suspend fun insert(grant: AccessGrant): Long

    /**
     * Returns the count of currently valid (unexpired) grants for a package.
     * A value > 0 means access should be permitted without re-gating.
     */
    @Query(
        "SELECT COUNT(*) FROM access_grants WHERE packageName = :packageName AND expiresAt > :now"
    )
    suspend fun activeGrantCount(packageName: String, now: Long): Int

    @Query(
        "SELECT * FROM access_grants WHERE packageName = :packageName AND expiresAt > :now ORDER BY expiresAt DESC LIMIT 1"
    )
    suspend fun latestActiveGrant(packageName: String, now: Long): AccessGrant?

    @Query("DELETE FROM access_grants WHERE expiresAt <= :now")
    suspend fun deleteExpired(now: Long): Int

    @Query("DELETE FROM access_grants")
    suspend fun deleteAll()

    @Query("SELECT * FROM access_grants")
    suspend fun getAll(): List<AccessGrant>
}
