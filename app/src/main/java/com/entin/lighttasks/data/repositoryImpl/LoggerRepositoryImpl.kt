package com.entin.lighttasks.data.repositoryImpl

import com.entin.lighttasks.data.util.logging.FirebaseCrashlyticsLog
import com.entin.lighttasks.data.util.logging.FirestoreLog
import com.entin.lighttasks.data.util.logging.LoggerModel
import com.entin.lighttasks.domain.repository.LogTag
import com.entin.lighttasks.domain.repository.LoggerRepository
import com.entin.lighttasks.presentation.util.getUserUid
import javax.inject.Inject

/**
 * Repository Implementation of [LoggerRepository] interface
 */

class LoggerRepositoryImpl @Inject constructor(
    private val crashlyticsLog: FirebaseCrashlyticsLog,
    firestoreLog: FirestoreLog,
) : LoggerRepository {

    /**
     * List of all logging services in App
     */
    private val allLoggingServices = listOf(
        crashlyticsLog,
        firestoreLog,
    )

    /**
     * Sending log model into all services
     */
    override suspend fun log(exception: Throwable?, message: String?, tag: LogTag) {
        makeLog(
            createLogModel(
                tagValue = tag.value,
                message = message,
                exception = exception,
            )
        )
    }

    /**
     * Sending log model into Crashlytics only from Timber
     */
    override suspend fun logTimber(exception: Throwable) {
        crashlyticsLog.reportTimber(exception)
    }

    /**
     * Log Model creation
     */
    private fun createLogModel(
        tagValue: String,
        message: String?,
        exception: Throwable?
    ): LoggerModel {
        return LoggerModel(
            userId = getUserUid(),
            tag = tagValue,
            message = message,
            exception = exception,
        )
    }

    /**
     * Send Log to Firebase collection LOGS
     */
    private suspend fun makeLog(loggerModel: LoggerModel) {
        allLoggingServices.forEach { logger ->
            logger.report(loggerModel)
        }
    }
}