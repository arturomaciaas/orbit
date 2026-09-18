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

/**
 * Lifecycle stage of the *active planet* the user is currently growing.
 *
 * Every planet begins at [PLANET] and grows through [MOON]. Ringed planet types
 * continue to [RINGS]; non-ringed types are complete at [MOON]. When a planet
 * reaches its type's final stage it "locks in" to the solar system and a new
 * active planet begins.
 *
 * (Historically this enum also carried SYSTEM/GALAXY, but those are now modeled
 * as separate zoom levels — the solar system and the galaxy — rather than stages
 * of a single morphing body.)
 */
enum class PlanetStage {
    PLANET,
    MOON,
    RINGS,
}

/**
 * The five distinct kinds of planet the user can grow. Types differ in size,
 * rings, moons, surface treatment, and one deliberate outlier ([ROGUE]).
 *
 * [finalStage] is the stage at which a planet of this type is considered complete:
 *  - Non-ringed types ([TERRAN], [DWARF]) complete at [PlanetStage.MOON].
 *  - Ringed types ([RINGED_GIANT], [ICE_GIANT], [ROGUE]) complete at [PlanetStage.RINGS].
 */
enum class PlanetType(val finalStage: PlanetStage, val hasRings: Boolean) {
    /** Mid-size, banded surface, one moon. Common. */
    TERRAN(finalStage = PlanetStage.MOON, hasRings = false),

    /** Small, cratered, captures a tiny asteroid as its "moon". Common. */
    DWARF(finalStage = PlanetStage.MOON, hasRings = false),

    /** Large, bold wide rings, one moon. */
    RINGED_GIANT(finalStage = PlanetStage.RINGS, hasRings = true),

    /** Medium, pale, thin steeply-tilted rings, two small moons. */
    ICE_GIANT(finalStage = PlanetStage.RINGS, hasRings = true),

    /** The "crazy" outlier: erratic lava/storm world with an elongated wobbling ring. Rare. */
    ROGUE(finalStage = PlanetStage.RINGS, hasRings = true),
}

/**
 * Which zoom level the Cosmos screen is currently showing.
 *
 * - [PLANET]: close-up of the active planet being grown right now.
 * - [SOLAR_SYSTEM]: the central star plus the completed planets orbiting it (progress toward 8).
 * - [GALAXY]: the collection of completed solar systems, each rendered as a distant star.
 */
enum class CosmosView {
    PLANET,
    SOLAR_SYSTEM,
    GALAXY,
}
