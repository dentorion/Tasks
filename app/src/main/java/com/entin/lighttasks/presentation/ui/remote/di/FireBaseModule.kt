package com.entin.lighttasks.presentation.ui.remote.di

import com.entin.lighttasks.presentation.util.ERROR_NAME_HILT
import com.entin.lighttasks.presentation.util.TASKS
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * Providing Firebase collections, they are lightweight
 */

@Module
@InstallIn(SingletonComponent::class)
object FireBaseModule {

    /**
     * Concerts firebase collection
     */
    @Named(TASKS)
    @Provides
    @Singleton
    fun provideFirebaseConcerts(): CollectionReference =
        Firebase.firestore.collection(TASKS)

    /**
     * Errors collection
     */
    @Named(ERROR_NAME_HILT)
    @Provides
    @Singleton
    fun provideFirebaseErrors(): CollectionReference =
        Firebase.firestore.collection(ERROR_NAME_HILT)
}
