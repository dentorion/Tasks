package com.entin.lighttasks.data.util.logging

import com.entin.lighttasks.presentation.util.getUserUid
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * Log model for Timber logging to Firebase, Crashlytics
 */

data class LoggerModel(
    val priority: Int? = 0,
    val tag: String? = null,
    val message: String? = null,
    val exception: Throwable? = null,
    val userId: String = getUserUid(),
    @ServerTimestamp
    val create: Date? = null,
)