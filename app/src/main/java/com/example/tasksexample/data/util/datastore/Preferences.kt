package com.example.tasksexample.data.util.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import com.example.tasksexample.domain.entity.OrderSort
import com.example.tasksexample.domain.entity.SortDataObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

    private object Keys {
        val KEY_SORT_SHOW_FINISHED_SHOW = booleanPreferencesKey("finished_show")
        val KEY_SORT_SHOW_TITLE_DATE_IMPORTANCE = stringPreferencesKey("sort_show")
    }

    val preferencesFlow = context.dataStore.data
        .catch {
            emit(emptyPreferences())
        }
        .map { preferences ->
            val sortFinished = preferences[Keys.KEY_SORT_SHOW_FINISHED_SHOW] ?: false
            val sortTitleDate = OrderSort.valueOf(
                preferences[Keys.KEY_SORT_SHOW_TITLE_DATE_IMPORTANCE] ?: OrderSort.SORT_BY_DATE.name
            )
            SortDataObject(sortTitleDate, sortFinished)
        }

    suspend fun updateFinishedSort(argument: Boolean) {
        context.dataStore.edit { settings ->
            settings[Keys.KEY_SORT_SHOW_FINISHED_SHOW] = argument
        }
    }

    suspend fun updateImportantOrder(argument: OrderSort) {
        context.dataStore.edit { settings ->
            settings[Keys.KEY_SORT_SHOW_TITLE_DATE_IMPORTANCE] = argument.name
        }
    }

    suspend fun updateOrderSort(argument: OrderSort) {
        context.dataStore.edit { settings ->
            settings[Keys.KEY_SORT_SHOW_TITLE_DATE_IMPORTANCE] = argument.name
        }
    }
}