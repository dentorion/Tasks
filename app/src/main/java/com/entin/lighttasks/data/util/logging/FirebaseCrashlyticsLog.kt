package com.entin.lighttasks.data.util.logging

import com.entin.lighttasks.presentation.util.getUserUid
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.crashlytics.ktx.setCustomKeys
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

/**
 * Firebase Crashlytics queries for logging situations
 */

class FirebaseCrashlyticsLog @Inject constructor() : Logger {
    /**
     * Add log report to Firebase Crashlytics service
     */
    override suspend fun report(loggerModel: LoggerModel) {
        if (loggerModel.exception != null) {
            Firebase.crashlytics.apply {
                setUserId(getUserUid())
                setCustomKeys {
                    loggerModel.apply {
                        key(Keys.USER_ID, userId)
                        tag?.let { key(Keys.TAG, it) }
                        message?.let { key(Keys.MESSAGE, it) }
                        exception?.let { exception ->
                            recordException(exception)
                            exception.message?.let { message ->
                                key(Keys.EXCEPTION_MESSAGE, message)
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun reportTimber(exception: Throwable) {
        report(
            LoggerModel(
                exception = exception,
                message = exception.message ?: ""
            )
        )
    }

    private object Keys {
        const val TAG = "tag"
        const val MESSAGE = "message"
        const val USER_ID = "userId"
        const val EXCEPTION_MESSAGE = "exception"
    }
}