package com.orbit.blocker.data.apps

import android.graphics.drawable.Drawable

/**
 * A launchable app discovered on the device. [icon] is loaded lazily by the UI layer
 * and is not persisted; only [packageName] and [label] are stored when the user
 * chooses to manage the app.
 */
data class InstalledApp(
    val packageName: String,
    val label: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false,
)
