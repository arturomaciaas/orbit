package com.orbit.blocker.domain.quiz

import com.orbit.blocker.data.model.QuizTopic

/** UI-facing display names for quiz topics. */
val QuizTopic.displayName: String
    get() = when (this) {
        QuizTopic.SYSTEM_DESIGN -> "System Design"
        QuizTopic.CHESS -> "Chess"
        QuizTopic.SOFTWARE_ENGINEERING -> "Software Engineering"
        QuizTopic.AWS -> "AWS"
    }
