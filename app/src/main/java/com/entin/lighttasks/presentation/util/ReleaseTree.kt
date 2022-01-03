package com.entin.lighttasks.presentation.util

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import org.jetbrains.annotations.NotNull
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Timber Tree
 * Error Model class
 * Firebase
 */

@Singleton
class ReleaseTree @Inject constructor(
    private val errorDb: ErrorsQueries,
) : @NotNull Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val error = ErrorFirebaseModel(
            priority = priority,
            tag = tag,
            message = message,
            t = t,
            userId = Firebase.auth.currentUser?.uid,
        )
        /**
         * Sending error reports to: Crashlytics and Firebase
         */
        if (priority == Log.ERROR || priority == Log.WARN) {
            // Save to Errors
            errorDb.error(error)
            // Save to Crashlytics
            FirebaseCrashlytics.getInstance().log(message)
        }
    }
}

/**
 * Error model for Timber logging to Firebase, Crashlytics
 */

data class ErrorFirebaseModel(
    val priority: Int? = null,
    val tag: String? = null,
    val message: String? = null,
    val t: Throwable? = null,
    val userId: String? = null,
    @ServerTimestamp
    val create: Date? = null,
)

/**
 * Firebase query for starting new concert
 */

@Singleton
class ErrorsQueries @Inject constructor(
    @Named(ERROR_NAME_HILT) private val fireBaseDbErrors: CollectionReference,
) {
    /**
     * Add error report
     */
    fun error(errorFirebase: ErrorFirebaseModel) {
        fireBaseDbErrors.document().set(errorFirebase, SetOptions.merge())
    }
}