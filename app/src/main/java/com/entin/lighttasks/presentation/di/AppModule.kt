package com.entin.lighttasks.presentation.di

import android.content.Context
import androidx.room.Room
import com.entin.lighttasks.data.db.AppDataBase
import com.entin.lighttasks.data.db.TaskDao
import com.entin.lighttasks.data.repository.TasksRepositoryImpl
import com.entin.lighttasks.domain.repository.TasksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
object AppModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "notes-data-base",
        )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideDao(db: AppDataBase): TaskDao = db.getTaskDAO()

    @Singleton
    @Provides
    fun provideRepository(dao: TaskDao): TasksRepository {
        return TasksRepositoryImpl(dao)
    }

    @Singleton
    @Provides
    @Named("AppScopeDI")
    fun provideAppScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
