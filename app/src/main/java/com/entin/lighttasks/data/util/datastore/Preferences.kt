package com.entin.lighttasks.data.util.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.SortPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * DataStore initialize
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

    /**
     * Reading saved parameters of sort order and filter flag
     */
    val preferencesFlow = context.dataStore.data
        .catch {
            emit(emptyPreferences())
        }
        .map { preferences ->
            val sortFinished = preferences[Keys.KEY_SORT_HIDE_FINISHED] ?: false
            val sortOrder = OrderSort.valueOf(
                preferences[Keys.KEY_SORT_BY_TITLE_DATE_IMPORTANCE_MANUAL]
                    ?: OrderSort.SORT_BY_DATE.name
            )
            SortPreferences(sortOrder, sortFinished)
        }

    /**
     * Update result list filter by finished flag
     */
    suspend fun updateFinishedSort(hideFinished: Boolean) {
        context.dataStore.edit { settings ->
            settings[Keys.KEY_SORT_HIDE_FINISHED] = hideFinished
        }
    }

    /**
     * Update sort order by
     *  - Title
     *  - Date creation
     *  - Importance
     *  - Manual
     */
    suspend fun updateSortOrder(argument: OrderSort) {
        context.dataStore.edit { settings ->
            settings[Keys.KEY_SORT_BY_TITLE_DATE_IMPORTANCE_MANUAL] = argument.name
        }
    }

    private object Keys {
        val KEY_SORT_HIDE_FINISHED = booleanPreferencesKey("finished_show")
        val KEY_SORT_BY_TITLE_DATE_IMPORTANCE_MANUAL = stringPreferencesKey("sort_show")
    }
}