package com.orbit.blocker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Terminal outcome of a focus session. */
enum class FocusOutcome {
    COMPLETED,
    ABORTED,
}

/**
 * History record of a focus session, written when the session ends. Completed
 * sessions feed gamification growth (Task 7) and appear in stats/backup.
 */
@Entity(
    tableName = "focus_sessions",
    indices = [Index("startedAt")],
)
data class FocusSessionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long,
    val endedAt: Long,
    /** Planned duration in millis (may exceed actual elapsed if aborted early). */
    val plannedDurationMillis: Long,
    val outcome: FocusOutcome,
    val blockedPackageCount: Int,
)
