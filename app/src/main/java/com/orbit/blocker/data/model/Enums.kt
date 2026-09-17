package com.orbit.blocker.data.model

/**
 * How an app is blocked.
 *
 * - [DURATION]: blocked until a fixed expiry timestamp (e.g. "block for 2 hours / 3 days").
 * - [FOCUS_SESSION]: blocked only while a focus session that includes it is active.
 */
enum class BlockMode {
    DURATION,
    FOCUS_SESSION,
}

/** Knowledge categories for the quiz gate question bank. */
enum class QuizTopic {
    SYSTEM_DESIGN,
    CHESS,
    SOFTWARE_ENGINEERING,
    AWS,
}

/**
 * Notification handling tier for a given app.
 *
 * - [PRIORITY]: pass through untouched, with sound (e.g. Phone, WhatsApp calls).
 * - [PEEK]: cancel the original and repost a trimmed, silent notification (sender + preview).
 * - [SUPPRESS]: cancel and store in the in-app digest; nothing shown live.
 */
enum class NotificationTier {
    PRIORITY,
    PEEK,
    SUPPRESS,
}

/** Stage of the space-themed gamification progression. */
enum class GalaxyStage {
    PLANET,
    MOON,
    RINGS,
    SYSTEM,
    GALAXY,
}
