package com.entin.lighttasks.presentation.application

import android.app.Application
import com.entin.lighttasks.BuildConfig
import com.entin.lighttasks.presentation.util.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {

    // For timber to save logs
    @Inject
    lateinit var releaseTree: ReleaseTree

    override fun onCreate() {
        super.onCreate()

        /**
         * Timber
         */
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return String.format(
                        "Timber. Class:%s: Line: %s, Method: %s",
                        super.createStackElementTag(element),
                        element.lineNumber,
                        element.methodName
                    )
                }
            })
        } else {
            Timber.plant(releaseTree)
        }
    }
}