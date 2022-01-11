package com.entin.lighttasks.domain.repository

/**
 * Interface of LoggerRepository
 */

interface LoggerRepository {

    suspend fun log(exception: Throwable?, message: String?, tag: LogTag)

    suspend fun logTimber(exception: Throwable)
}

/**
 * Tag
 */
sealed class LogTag(val value: String) {
    /**
     * Authorization
     */
    sealed class Auth(value: String) : LogTag(value) {
        data class LogIn(val name: String = "login") : Auth(name)
        data class LogOut(val name: String = "logout") : Auth(name)
    }

    /**
     * Download all cloud tasks
     */
    data class DownloadCloudTasks(val name: String = "download cloud tasks") : LogTag(name)
}