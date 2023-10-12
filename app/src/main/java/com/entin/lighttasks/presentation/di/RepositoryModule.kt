package com.entin.lighttasks.presentation.di

import com.entin.lighttasks.data.db.AlarmsDao
import com.entin.lighttasks.data.db.SectionsDao
import com.entin.lighttasks.data.db.TaskDao
import com.entin.lighttasks.data.db.TaskGroupsDao
import com.entin.lighttasks.data.repository.AlarmsRepositoryImpl
import com.entin.lighttasks.data.repository.SectionsRepositoryImpl
import com.entin.lighttasks.data.repository.TasksRepositoryImpl
import com.entin.lighttasks.domain.repository.AlarmsRepository
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton

/**
 * DaggerHilt Module. All in One, because of small application.
 * - DataBase
 * - DAO
 * - Repository
 * - Remote Repository
 * - Scope using in AllTasksViewModel for deleting task from Dialog
 *   to be sure operation will be finished after dialog closing.
 */

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideTasksRepository(
        taskDao: TaskDao,
        taskGroupsDao: TaskGroupsDao,
    ): TasksRepository {
        return TasksRepositoryImpl(taskDao, taskGroupsDao)
    }

    @Singleton
    @Provides
    fun provideSectionsRepository(
        sectionsDao: SectionsDao,
    ): SectionsRepository {
        return SectionsRepositoryImpl(sectionsDao)
    }

    @Singleton
    @Provides
    fun provideAlarmsRepository(
        alarmsDao: AlarmsDao,
    ): AlarmsRepository {
        return AlarmsRepositoryImpl(alarmsDao)
    }

    @Singleton
    @Provides
    @Named("AppScopeDI")
    fun provideAppScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
