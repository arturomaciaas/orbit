package com.orbit.blocker.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "orbit_settings")

/**
 * User-tunable settings backed by DataStore. Defaults match the plan:
 * a quiz requires answering questions correctly to earn a 5-minute access window.
 */
@Singleton
class OrbitSettings @Inject constructor(
    private val context: Context,
) {
    private object Keys {
        val QUESTIONS_REQUIRED = intPreferencesKey("questions_required")
        val ACCESS_WINDOW_MILLIS = longPreferencesKey("access_window_millis")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }

    val questionsRequired: Flow<Int> = context.dataStore.data
        .map { it[Keys.QUESTIONS_REQUIRED] ?: DEFAULT_QUESTIONS_REQUIRED }

    val accessWindowMillis: Flow<Long> = context.dataStore.data
        .map { it[Keys.ACCESS_WINDOW_MILLIS] ?: DEFAULT_ACCESS_WINDOW_MILLIS }

    val onboardingComplete: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.ONBOARDING_COMPLETE] ?: false }

    suspend fun setOnboardingComplete(value: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = value }
    }

    suspend fun setQuestionsRequired(value: Int) {
        context.dataStore.edit { it[Keys.QUESTIONS_REQUIRED] = value.coerceIn(1, 20) }
    }

    suspend fun setAccessWindowMillis(value: Long) {
        context.dataStore.edit { it[Keys.ACCESS_WINDOW_MILLIS] = value }
    }

    companion object {
        const val DEFAULT_QUESTIONS_REQUIRED = 3
        const val DEFAULT_ACCESS_WINDOW_MILLIS = 5 * 60 * 1000L // 5 minutes
    }
}
