package com.entin.lighttasks.presentation.util

import android.util.Log
import com.entin.lighttasks.domain.repository.LoggerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.NotNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Timber Tree
 */

@Singleton
class ReleaseTree @Inject constructor(
    private val loggerRepository: LoggerRepository,
    @Named("AppScopeDI") private val diAppScope: CoroutineScope,
) : @NotNull Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        /**
         * Sending error reports to Crashlytics
         */
        if (priority == Log.ERROR){
            val logTimber = t ?: Exception(message)
            diAppScope.launch {
                loggerRepository.logTimber(logTimber)
            }
        }
    }
}