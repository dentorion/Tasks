package com.entin.lighttasks.presentation.di

import android.content.Context
import androidx.room.Room
import com.entin.lighttasks.data.db.DataBase
import com.entin.lighttasks.data.db.DataBase.Companion.DATABASE_NAME
import com.entin.lighttasks.data.db.TaskDao
import com.entin.lighttasks.data.db.TaskGroupsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(
        @ApplicationContext context: Context,
        provider: Provider<TaskGroupsDao>,
    ): DataBase =
        Room.databaseBuilder(
            context,
            DataBase::class.java,
            DATABASE_NAME,
        )
            .addMigrations(DbMigration().migrationFrom1To2)
            .addCallback(TaskGroupsCallback(provider))
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideTaskDao(db: DataBase): TaskDao = db.getTaskDAO()

    @Singleton
    @Provides
    fun provideTaskGroupsDao(db: DataBase): TaskGroupsDao = db.getTaskGroupsDAO()
}