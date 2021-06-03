package com.example.tasksexample.presentation.util

import android.content.Context
import android.content.res.Configuration
import com.example.tasksexample.presentation.util.AppConstants.LANGUAGES_COUNTRY
import com.example.tasksexample.presentation.util.AppConstants.LANGUAGES_DEFAULT
import com.example.tasksexample.presentation.util.AppConstants.LANGUAGES_KEY
import java.util.*

object RuntimeLocaleChanger {

    fun wrapContext(context: Context): Context {

        val lang = context.getSharedPreferences(LANGUAGES_KEY, Context.MODE_PRIVATE)
            .get(LANGUAGES_KEY, LANGUAGES_DEFAULT)

        val savedLocale = Locale(LANGUAGES_COUNTRY[lang])
        Locale.setDefault(savedLocale)
        val newConfig = Configuration()
        newConfig.setLocale(savedLocale)
        return context.createConfigurationContext(newConfig)
    }

    fun overrideLocale(context: Context) {

        val lang = context.getSharedPreferences(LANGUAGES_KEY, Context.MODE_PRIVATE)
            .get(LANGUAGES_KEY, LANGUAGES_DEFAULT)

        val savedLocale = Locale(LANGUAGES_COUNTRY[lang])

        Locale.setDefault(savedLocale)
        val newConfig = Configuration()
        newConfig.setLocale(savedLocale)
        context.createConfigurationContext(newConfig)

        if (context != context.applicationContext) {
            context.applicationContext.run { createConfigurationContext(newConfig) }
        }
    }

}