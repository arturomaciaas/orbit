package com.orbit.blocker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.orbit.blocker.R
import com.orbit.blocker.domain.block.DurationFormatter
import com.orbit.blocker.domain.focus.FocusController
import com.orbit.blocker.domain.focus.FocusSessionManager
import com.orbit.blocker.domain.focus.FocusTimer
import com.orbit.blocker.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foreground service that runs the focus-session countdown and shows a persistent
 * notification with the remaining time. On completion it delegates to [FocusController]
 * to record the session and grow the galaxy, then stops itself.
 */
@AndroidEntryPoint
class FocusSessionService : android.app.Service() {

    @Inject lateinit var focusController: FocusController
    @Inject lateinit var focusSessionManager: FocusSessionManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var tickJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val packages = intent.getStringArrayExtra(EXTRA_PACKAGES)?.toSet() ?: emptySet()
                val durationMillis = intent.getLongExtra(EXTRA_DURATION_MILLIS, 0L)
                startSession(packages, durationMillis)
            }
            ACTION_STOP -> {
                scope.launch {
                    focusController.abort()
                    stopSelfSafely()
                }
            }
        }
        return START_STICKY
    }

    private fun startSession(packages: Set<String>, durationMillis: Long) {
        val now = System.currentTimeMillis()
        val endsAt = now + durationMillis
        focusController.begin(packages, startedAt = now, endsAt = endsAt)

        startForegroundWithNotification(buildNotification(durationMillis))

        tickJob?.cancel()
        tickJob = scope.launch {
            while (isActive) {
                val nowTick = System.currentTimeMillis()
                if (FocusTimer.isComplete(endsAt, nowTick)) {
                    focusController.complete(nowTick)
                    updateNotification(buildCompletedNotification())
                    stopSelfSafely()
                    break
                }
                val remaining = FocusTimer.remainingMillis(endsAt, nowTick)
                updateNotification(buildNotification(remaining))
                delay(1_000)
            }
        }
    }

    private fun startForegroundWithNotification(notification: Notification) {
        ensureChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification(notification: Notification) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(remainingMillis: Long): Notification {
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        // No stop action: a focus session is a hard commitment. The only way "out" is to
        // wait for the timer or pass the quiz gate on a blocked app.
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Focus session in progress")
            .setContentText("${DurationFormatter.format(remainingMillis)} remaining")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)
            .build()
    }

    private fun buildCompletedNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Focus session complete")
            .setContentText("Your cosmos grew. Nice work.")
            .setAutoCancel(true)
            .build()

    private fun ensureChannel() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Focus sessions",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply { description = "Shows the running focus-session timer" }
            )
        }
    }

    private fun stopSelfSafely() {
        tickJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        tickJob?.cancel()
        scope.coroutineContext[Job]?.cancel()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "orbit_focus_session"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.orbit.blocker.action.START_FOCUS"
        const val ACTION_STOP = "com.orbit.blocker.action.STOP_FOCUS"
        private const val EXTRA_PACKAGES = "extra_packages"
        private const val EXTRA_DURATION_MILLIS = "extra_duration_millis"

        fun start(context: Context, packages: Set<String>, durationMillis: Long) {
            val intent = Intent(context, FocusSessionService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_PACKAGES, packages.toTypedArray())
                putExtra(EXTRA_DURATION_MILLIS, durationMillis)
            }
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, FocusSessionService::class.java).setAction(ACTION_STOP)
            context.startService(intent)
        }
    }
}
