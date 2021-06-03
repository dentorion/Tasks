package com.example.tasksexample.presentation.application

import android.app.Application
import android.content.Context
import com.example.tasksexample.presentation.util.RuntimeLocaleChanger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(RuntimeLocaleChanger.wrapContext(base))
    }

}