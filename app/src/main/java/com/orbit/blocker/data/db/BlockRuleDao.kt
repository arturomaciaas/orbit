package com.orbit.blocker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.BlockRule
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: BlockRule): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<BlockRule>)

    @Update
    suspend fun update(rule: BlockRule)

    @Query("DELETE FROM block_rules WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM block_rules WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)

    @Query("SELECT * FROM block_rules WHERE enabled = 1 ORDER BY createdAt DESC")
    fun observeEnabled(): Flow<List<BlockRule>>

    @Query("SELECT * FROM block_rules ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<BlockRule>>

    @Query("SELECT * FROM block_rules WHERE packageName = :packageName AND enabled = 1")
    suspend fun getEnabledForPackage(packageName: String): List<BlockRule>

    @Query("SELECT * FROM block_rules")
    suspend fun getAll(): List<BlockRule>

    @Query("DELETE FROM block_rules")
    suspend fun deleteAll()

    /**
     * Disables expired DURATION rules whose expiry has passed. Returns the number
     * of rules cleared. Used by the WorkManager cleanup job (Task 2).
     */
    @Query(
        """
        UPDATE block_rules
        SET enabled = 0
        WHERE mode = :durationMode
          AND enabled = 1
          AND expiresAt IS NOT NULL
          AND expiresAt <= :now
        """
    )
    suspend fun disableExpiredDurationRules(
        now: Long,
        durationMode: BlockMode = BlockMode.DURATION,
    ): Int
}
