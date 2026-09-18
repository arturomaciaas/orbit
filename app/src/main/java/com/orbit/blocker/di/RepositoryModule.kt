package com.orbit.blocker.di

import android.content.Context
import com.orbit.blocker.data.repository.AccessGrantRepository
import com.orbit.blocker.data.repository.AccessGrantRepositoryImpl
import com.orbit.blocker.data.repository.BlockRepository
import com.orbit.blocker.data.repository.BlockRepositoryImpl
import com.orbit.blocker.data.repository.FocusSessionRepository
import com.orbit.blocker.data.repository.FocusSessionRepositoryImpl
import com.orbit.blocker.data.repository.GalaxyRepository
import com.orbit.blocker.data.repository.GalaxyRepositoryImpl
import com.orbit.blocker.data.repository.NotificationRepository
import com.orbit.blocker.data.repository.NotificationRepositoryImpl
import com.orbit.blocker.data.repository.QuestionRepository
import com.orbit.blocker.data.repository.QuestionRepositoryImpl
import com.orbit.blocker.data.settings.OrbitSettings
import com.orbit.blocker.domain.focus.FocusSessionStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindBlockRepository(impl: BlockRepositoryImpl): BlockRepository

    @Binds @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds @Singleton
    abstract fun bindAccessGrantRepository(impl: AccessGrantRepositoryImpl): AccessGrantRepository

    @Binds @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds @Singleton
    abstract fun bindGalaxyRepository(impl: GalaxyRepositoryImpl): GalaxyRepository

    @Binds @Singleton
    abstract fun bindFocusSessionRepository(impl: FocusSessionRepositoryImpl): FocusSessionRepository

    @Binds @Singleton
    abstract fun bindFocusSessionStore(impl: OrbitSettings): FocusSessionStore

    companion object {
        @Provides
        @Singleton
        fun provideSettings(@ApplicationContext context: Context): OrbitSettings =
            OrbitSettings(context)
    }
}
