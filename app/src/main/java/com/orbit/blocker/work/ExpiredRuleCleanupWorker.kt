package com.orbit.blocker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.orbit.blocker.data.repository.AccessGrantRepository
import com.orbit.blocker.data.repository.BlockRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Periodically disables DURATION block rules whose expiry has passed and purges
 * expired access grants. This keeps the DB tidy even if the app isn't opened; the
 * interception engine (Task 4) also checks expiry live, so this is a backstop.
 */
@HiltWorker
class ExpiredRuleCleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val blockRepository: BlockRepository,
    private val accessGrantRepository: AccessGrantRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        blockRepository.clearExpiredDurationRules(now)
        accessGrantRepository.purgeExpired(now)
        return Result.success()
    }

    companion object {
        private const val UNIQUE_NAME = "orbit_expired_rule_cleanup"

        /** Schedules the periodic cleanup (idempotent; keeps any existing schedule). */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<ExpiredRuleCleanupWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
