package com.entin.lighttasks.presentation.application

import android.app.Application
import android.content.Context
import com.entin.lighttasks.presentation.util.RuntimeLocaleChanger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(RuntimeLocaleChanger.wrapContext(base))
    }
}