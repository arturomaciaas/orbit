package com.orbit.blocker.data.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enumerates launchable apps via [PackageManager]. Excludes Orbit itself. Runs on
 * the IO dispatcher because querying + loading labels can be slow on large devices.
 */
@Singleton
class InstalledAppsProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun loadLaunchableApps(includeIcons: Boolean = true): List<InstalledApp> =
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val resolved = pm.queryIntentActivities(intent, 0)

            resolved.asSequence()
                .mapNotNull { info ->
                    val appInfo = info.activityInfo?.applicationInfo ?: return@mapNotNull null
                    val pkg = appInfo.packageName
                    if (pkg == context.packageName) return@mapNotNull null
                    InstalledApp(
                        packageName = pkg,
                        label = appInfo.loadLabel(pm).toString(),
                        icon = if (includeIcons) runCatching { appInfo.loadIcon(pm) }.getOrNull() else null,
                        isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                    )
                }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase() }
                .toList()
        }
}
