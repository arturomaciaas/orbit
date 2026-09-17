package com.orbit.blocker.domain.block

import java.util.concurrent.TimeUnit

/** Formats a millisecond duration as a compact human string, e.g. "2d 3h", "45m", "30s". */
object DurationFormatter {

    fun format(millis: Long): String {
        if (millis <= 0L) return "expired"
        val days = TimeUnit.MILLISECONDS.toDays(millis)
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            days > 0 -> buildString {
                append("${days}d")
                if (hours > 0) append(" ${hours}h")
            }
            hours > 0 -> buildString {
                append("${hours}h")
                if (minutes > 0) append(" ${minutes}m")
            }
            minutes > 0 -> "${minutes}m"
            else -> "${seconds}s"
        }
    }
}
