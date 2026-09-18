package com.orbit.blocker.data.db

import androidx.room.TypeConverter
import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.FocusOutcome
import com.orbit.blocker.data.model.NotificationTier
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.data.model.PlanetType
import com.orbit.blocker.data.model.QuizTopic

/**
 * Room type converters. Enums are stored by name for stability across ordinal changes.
 * The list of answer choices is stored as a single string using a unit-separator
 * delimiter (0x1F) that will not appear in normal question text.
 */
class Converters {

    // region enums
    @TypeConverter
    fun blockModeToString(value: BlockMode): String = value.name

    @TypeConverter
    fun stringToBlockMode(value: String): BlockMode = BlockMode.valueOf(value)

    @TypeConverter
    fun quizTopicToString(value: QuizTopic): String = value.name

    @TypeConverter
    fun stringToQuizTopic(value: String): QuizTopic = QuizTopic.valueOf(value)

    @TypeConverter
    fun notificationTierToString(value: NotificationTier): String = value.name

    @TypeConverter
    fun stringToNotificationTier(value: String): NotificationTier = NotificationTier.valueOf(value)

    @TypeConverter
    fun planetStageToString(value: PlanetStage): String = value.name

    @TypeConverter
    fun stringToPlanetStage(value: String): PlanetStage = PlanetStage.valueOf(value)

    @TypeConverter
    fun planetTypeToString(value: PlanetType): String = value.name

    @TypeConverter
    fun stringToPlanetType(value: String): PlanetType = PlanetType.valueOf(value)

    @TypeConverter
    fun focusOutcomeToString(value: FocusOutcome): String = value.name

    @TypeConverter
    fun stringToFocusOutcome(value: String): FocusOutcome = FocusOutcome.valueOf(value)
    // endregion

    // region choices list
    @TypeConverter
    fun choicesToString(choices: List<String>): String = choices.joinToString(DELIMITER)

    @TypeConverter
    fun stringToChoices(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(DELIMITER)
    // endregion

    companion object {
        private const val DELIMITER = "\u001F"
    }
}
