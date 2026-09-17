package com.orbit.blocker.data.backup

import android.content.Context
import android.net.Uri
import com.orbit.blocker.data.backup.BackupMappers.toDto
import com.orbit.blocker.data.backup.BackupMappers.toEntity
import com.orbit.blocker.data.db.OrbitDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exports/imports Orbit data as JSON. Reads and writes via the [OrbitDatabase] DAOs.
 * The JSON (de)serialization is split out into pure [toJson]/[fromJson] so it can be
 * unit-tested without Android, while [exportTo]/[importFrom] handle the SAF Uri I/O.
 */
@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: OrbitDatabase,
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /** Serializes the current database contents to a JSON string. */
    suspend fun buildBackup(now: Long = System.currentTimeMillis()): OrbitBackup =
        withContext(Dispatchers.IO) {
            OrbitBackup(
                exportedAt = now,
                managedApps = db.blockedAppDao().getAll().map { it.toDto() },
                blockRules = db.blockRuleDao().getAll().map { it.toDto() },
                questions = db.questionDao().getAll().map { it.toDto() },
                galaxy = db.galaxyProgressDao().get()?.toDto(),
                focusSessions = db.focusSessionDao().getAll().map { it.toDto() },
            )
        }

    fun toJson(backup: OrbitBackup): String = json.encodeToString(backup)

    fun fromJson(text: String): OrbitBackup = json.decodeFromString(text)

    /** Writes a full backup to [target] (a SAF document Uri). */
    suspend fun exportTo(target: Uri) = withContext(Dispatchers.IO) {
        val backup = buildBackup()
        val bytes = toJson(backup).toByteArray(Charsets.UTF_8)
        context.contentResolver.openOutputStream(target)?.use { it.write(bytes) }
            ?: error("Could not open output stream for $target")
    }

    /**
     * Restores from [source] (a SAF document Uri), replacing existing rows. Managed
     * apps/rules, questions, galaxy, and focus history are overwritten; access grants
     * and notification digest are intentionally not part of the backup.
     */
    suspend fun importFrom(source: Uri) = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(source)?.use {
            it.readBytes().toString(Charsets.UTF_8)
        } ?: error("Could not open input stream for $source")

        val backup = fromJson(text)
        applyBackup(backup)
    }

    /** Replaces database contents with [backup]. */
    suspend fun applyBackup(backup: OrbitBackup) = withContext(Dispatchers.IO) {
        // Replace managed apps + rules.
        db.blockRuleDao().deleteAll()
        db.blockedAppDao().deleteAll()
        db.blockedAppDao().insertAll(backup.managedApps.map { it.toEntity() })
        db.blockRuleDao().insertAll(backup.blockRules.map { it.toEntity() })

        // Replace question bank.
        db.questionDao().let { dao ->
            dao.getAll().forEach { dao.delete(it) }
            dao.insertAll(backup.questions.map { it.toEntity() })
        }

        // Replace galaxy progress (singleton row).
        backup.galaxy?.let { db.galaxyProgressDao().upsert(it.toEntity()) }

        // Replace focus history.
        db.focusSessionDao().deleteAll()
        db.focusSessionDao().insertAll(backup.focusSessions.map { it.toEntity() })
    }
}
