package com.orbit.blocker.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.orbit.blocker.domain.focus.FocusSessionState
import com.orbit.blocker.domain.focus.FocusSessionStore
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
) : FocusSessionStore {
    private object Keys {
        val QUESTIONS_REQUIRED = intPreferencesKey("questions_required")
        val ACCESS_WINDOW_MILLIS = longPreferencesKey("access_window_millis")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")

        // Durable active focus session, so enforcement survives process/service restarts.
        val FOCUS_ACTIVE = booleanPreferencesKey("focus_active")
        val FOCUS_PACKAGES = stringSetPreferencesKey("focus_packages")
        val FOCUS_STARTED_AT = longPreferencesKey("focus_started_at")
        val FOCUS_ENDS_AT = longPreferencesKey("focus_ends_at")
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

    /**
     * The persisted active focus session, or [FocusSessionState.INACTIVE] when none is stored.
     * This is the durable source of truth that lets the accessibility service keep gating
     * focus-blocked apps even after its process is recreated (the in-memory session state is
     * volatile and resets to INACTIVE on restart).
     */
    override val activeFocusSession: Flow<FocusSessionState> = context.dataStore.data.map { prefs ->
        if (prefs[Keys.FOCUS_ACTIVE] != true) {
            FocusSessionState.INACTIVE
        } else {
            FocusSessionState(
                active = true,
                blockedPackages = prefs[Keys.FOCUS_PACKAGES] ?: emptySet(),
                startedAt = prefs[Keys.FOCUS_STARTED_AT],
                endsAt = prefs[Keys.FOCUS_ENDS_AT],
            )
        }
    }

    /** Persists the active focus session so it survives process death. */
    override suspend fun saveActiveFocusSession(state: FocusSessionState) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FOCUS_ACTIVE] = state.active
            prefs[Keys.FOCUS_PACKAGES] = state.blockedPackages
            state.startedAt?.let { prefs[Keys.FOCUS_STARTED_AT] = it } ?: prefs.remove(Keys.FOCUS_STARTED_AT)
            state.endsAt?.let { prefs[Keys.FOCUS_ENDS_AT] = it } ?: prefs.remove(Keys.FOCUS_ENDS_AT)
        }
    }

    /** Clears any persisted active focus session. */
    override suspend fun clearActiveFocusSession() {
        context.dataStore.edit { prefs ->
            prefs[Keys.FOCUS_ACTIVE] = false
            prefs.remove(Keys.FOCUS_PACKAGES)
            prefs.remove(Keys.FOCUS_STARTED_AT)
            prefs.remove(Keys.FOCUS_ENDS_AT)
        }
    }

    companion object {
        const val DEFAULT_QUESTIONS_REQUIRED = 3
        const val DEFAULT_ACCESS_WINDOW_MILLIS = 5 * 60 * 1000L // 5 minutes

        /** Fixed access window granted by the "1-minute quick access" bypass. */
        const val QUICK_ACCESS_WINDOW_MILLIS = 60 * 1000L // 1 minute
    }
}
