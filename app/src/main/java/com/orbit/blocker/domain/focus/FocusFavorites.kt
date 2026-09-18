package com.orbit.blocker.domain.focus

import com.orbit.blocker.data.apps.InstalledApp

/**
 * Default "favorites" — apps that stay open during a focus session unless the user
 * removes them from the allow-list. A focus session blocks *everything else* by default.
 *
 * We match on both known package names and label keywords, because the exact package of an
 * app (e.g. a chess client) varies by vendor/device. Matching is best-effort and purely
 * about the initial default; the user can freely add or remove apps afterward.
 */
object FocusFavorites {

    /** A default favorite with the keywords used to recognize it among installed apps. */
    data class Favorite(
        val id: String,
        val displayName: String,
        val packageHints: List<String>,
        val labelKeywords: List<String>,
    )

    val DEFAULTS: List<Favorite> = listOf(
        Favorite(
            id = "whatsapp",
            displayName = "WhatsApp",
            packageHints = listOf("com.whatsapp"),
            labelKeywords = listOf("whatsapp"),
        ),
        Favorite(
            id = "phone",
            displayName = "Phone",
            packageHints = listOf(
                "com.samsung.android.dialer",
                "com.google.android.dialer",
                "com.android.dialer",
                "com.android.phone",
            ),
            labelKeywords = listOf("phone", "dialer"),
        ),
        Favorite(
            id = "spotify",
            displayName = "Spotify",
            packageHints = listOf("com.spotify.music"),
            labelKeywords = listOf("spotify"),
        ),
        Favorite(
            id = "lichess",
            displayName = "Lichess",
            packageHints = listOf("org.lichess.mobileapp", "com.lichess"),
            labelKeywords = listOf("lichess"),
        ),
        Favorite(
            id = "chess",
            displayName = "Chess",
            packageHints = listOf("com.chess"),
            labelKeywords = listOf("chess"),
        ),
    )

    /** True if [app] matches [favorite] by package hint or label keyword. */
    fun matches(app: InstalledApp, favorite: Favorite): Boolean {
        val pkg = app.packageName.lowercase()
        if (favorite.packageHints.any { pkg == it || pkg.startsWith("$it.") }) return true
        val label = app.label.lowercase()
        return favorite.labelKeywords.any { label.contains(it) }
    }

    /**
     * Resolves the default allow-list against the actual set of [installed] apps, returning
     * the package names that should stay open. Only apps that are actually installed are
     * included (so absent favorites simply don't appear).
     */
    fun defaultAllowedPackages(installed: List<InstalledApp>): Set<String> {
        val allowed = mutableSetOf<String>()
        for (fav in DEFAULTS) {
            installed.filter { matches(it, fav) }.forEach { allowed.add(it.packageName) }
        }
        return allowed
    }
}
