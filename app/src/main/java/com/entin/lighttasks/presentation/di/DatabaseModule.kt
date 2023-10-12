package com.entin.lighttasks.presentation.di

import android.content.Context
import androidx.room.Room
import com.entin.lighttasks.data.db.AlarmsDao
import com.entin.lighttasks.data.db.DataBase
import com.entin.lighttasks.data.db.DataBase.Companion.DATABASE_NAME
import com.entin.lighttasks.data.db.SectionsDao
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
        providerTasks: Provider<TaskDao>,
    ): DataBase =
        Room.databaseBuilder(
            context,
            DataBase::class.java,
            DATABASE_NAME,
        )
            .addMigrations(DbMigration().migrationFrom1To2)
            .addMigrations(DbMigration().migrationFrom2To3)
            .addMigrations(DbMigration().migrationFrom3To4)
            .addMigrations(DbMigration().migrationFrom4To5)
            .addMigrations(DbMigration().migrationFrom5To6)
            .addCallback(TaskGroupsCallback(provider))
            .build()

    @Singleton
    @Provides
    fun provideAlarmsDao(db: DataBase): AlarmsDao = db.getAlarmsDAO()

    @Singleton
    @Provides
    fun provideSectionDao(db: DataBase): SectionsDao = db.getSectionDAO()

    @Singleton
    @Provides
    fun provideTaskDao(db: DataBase): TaskDao = db.getTaskDAO()

    @Singleton
    @Provides
    fun provideTaskGroupsDao(db: DataBase): TaskGroupsDao = db.getTaskGroupsDAO()
}
