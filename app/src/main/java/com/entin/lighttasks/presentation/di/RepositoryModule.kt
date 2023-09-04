package com.entin.lighttasks.presentation.di

import com.entin.lighttasks.data.db.TaskDao
import com.entin.lighttasks.data.db.TaskGroupsDao
import com.entin.lighttasks.data.repository.TasksRepositoryImpl
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
    fun provideRepository(
        taskDao: TaskDao,
        taskGroupsDao: TaskGroupsDao,
    ): TasksRepository {
        return TasksRepositoryImpl(taskDao, taskGroupsDao)
    }

    @Singleton
    @Provides
    @Named("AppScopeDI")
    fun provideAppScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
