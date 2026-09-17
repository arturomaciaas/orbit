package com.orbit.blocker.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings

/** Helpers for checking and requesting Notification Listener access. */
object NotificationAccessPermission {

    fun isEnabled(context: Context): Boolean {
        val expected = ComponentName(context, OrbitNotificationListenerService::class.java)
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        ) ?: return false
        return flat.split(":").any {
            val cn = ComponentName.unflattenFromString(it)
            cn != null && cn == expected
        }
    }

    fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
