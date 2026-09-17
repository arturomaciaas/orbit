package com.orbit.blocker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.orbit.blocker.data.repository.GalaxyRepository
import com.orbit.blocker.data.seed.QuestionSeeder
import com.orbit.blocker.work.ExpiredRuleCleanupWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class OrbitApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var questionSeeder: QuestionSeeder
    @Inject lateinit var galaxyRepository: GalaxyRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Backstop cleanup for expired duration blocks and access grants.
        ExpiredRuleCleanupWorker.schedule(this)

        // First-run data setup: seed the quiz bank and ensure the galaxy row exists.
        appScope.launch {
            questionSeeder.seedIfEmpty()
            galaxyRepository.ensureInitialized()
        }
    }
}
