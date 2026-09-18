package com.orbit.blocker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.orbit.blocker.data.model.CompletedPlanet
import kotlinx.coroutines.flow.Flow

/**
 * Access to the completed planets that make up each solar system. Planets are scoped
 * by [CompletedPlanet.systemIndex] so a finished system's bodies persist while the next
 * system is built.
 */
@Dao
interface CompletedPlanetDao {

    @Insert
    suspend fun insert(planet: CompletedPlanet): Long

    @Query("SELECT * FROM completed_planets WHERE systemIndex = :systemIndex ORDER BY slot ASC")
    fun observeForSystem(systemIndex: Int): Flow<List<CompletedPlanet>>

    @Query("SELECT * FROM completed_planets WHERE systemIndex = :systemIndex ORDER BY slot ASC")
    suspend fun forSystem(systemIndex: Int): List<CompletedPlanet>

    @Query("SELECT * FROM completed_planets ORDER BY systemIndex ASC, slot ASC")
    fun observeAll(): Flow<List<CompletedPlanet>>

    @Query("SELECT * FROM completed_planets ORDER BY systemIndex ASC, slot ASC")
    suspend fun getAll(): List<CompletedPlanet>

    @Delete
    suspend fun delete(planet: CompletedPlanet)

    @Query("DELETE FROM completed_planets")
    suspend fun deleteAll()
}
