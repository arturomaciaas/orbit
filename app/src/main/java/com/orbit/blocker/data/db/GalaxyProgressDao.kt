package com.orbit.blocker.data.db

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.orbit.blocker.data.model.GalaxyProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface GalaxyProgressDao {

    @Upsert
    suspend fun upsert(progress: GalaxyProgress)

    @Query("SELECT * FROM galaxy_progress WHERE id = :id LIMIT 1")
    fun observe(id: Int = GalaxyProgress.SINGLETON_ID): Flow<GalaxyProgress?>

    @Query("SELECT * FROM galaxy_progress WHERE id = :id LIMIT 1")
    suspend fun get(id: Int = GalaxyProgress.SINGLETON_ID): GalaxyProgress?

    @androidx.room.Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(progress: GalaxyProgress)
}
