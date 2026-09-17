package com.orbit.blocker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.orbit.blocker.data.model.FocusSessionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert
    suspend fun insert(record: FocusSessionRecord): Long

    @Insert
    suspend fun insertAll(records: List<FocusSessionRecord>)

    @Query("SELECT * FROM focus_sessions ORDER BY startedAt DESC")
    fun observeAll(): Flow<List<FocusSessionRecord>>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE outcome = 'COMPLETED'")
    fun observeCompletedCount(): Flow<Int>

    @Query("SELECT * FROM focus_sessions")
    suspend fun getAll(): List<FocusSessionRecord>

    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAll()
}
