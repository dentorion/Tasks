package com.entin.lighttasks.data.util.logging

import com.entin.lighttasks.presentation.util.LOGS_NAME_HILT
import com.entin.lighttasks.presentation.util.getUserUid
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Firebase queries for logging situations into firestore collections
 */

class FirestoreLog @Inject constructor(
    @Named(LOGS_NAME_HILT) private val firebaseLogs: CollectionReference,
) : Logger {
    /**
     * Add log report to Firestore collection
     */
    override suspend fun report(loggerModel: LoggerModel) {
        try {
            firebaseLogs
                .document(getUserUid())
                .collection(LOGS_NAME_HILT)
                .document("${Date().time} :: ${loggerModel.tag ?: "-"}")
                .set(loggerModel)
                .await()
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }
}