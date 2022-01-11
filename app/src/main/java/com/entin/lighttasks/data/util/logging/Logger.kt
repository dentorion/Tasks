package com.entin.lighttasks.data.util.logging

/**
 * Interface of logging with different services in different ways.
 */
interface Logger {
    /**
     * Send report function
     */
    suspend fun report(loggerModel: LoggerModel)
}