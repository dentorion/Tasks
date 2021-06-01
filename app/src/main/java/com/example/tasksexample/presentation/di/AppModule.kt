package com.example.tasksexample.presentation.di

import android.content.Context
import androidx.room.Room
import com.example.tasksexample.data.db.AppDataBase
import com.example.tasksexample.data.db.TaskDao
import com.example.tasksexample.domain.repository.TasksRepository
import com.example.tasksexample.data.repositoryImpl.TasksRepositoryImpl
import com.example.tasksexample.data.util.datastore.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "notes-data-base"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideDao(db: AppDataBase): TaskDao = db.getTaskDAO()

    @Singleton
    @Provides
    fun provide(dao: TaskDao): TasksRepository {
        return TasksRepositoryImpl(dao)
    }

    @Singleton
    @Provides
    @Named("AppScopeDI")
    fun provideAppScope() = CoroutineScope(SupervisorJob())
}