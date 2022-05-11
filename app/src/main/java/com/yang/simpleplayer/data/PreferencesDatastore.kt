package com.yang.simpleplayer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


private const val PREFERENCES_DATASTORE = "preference_data_store"
private val Context.preferencesDatastore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_DATASTORE
)
object PreferencesDatastore {
    suspend fun save(context:Context, key: String, value:Long) {
        context.preferencesDatastore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }

    suspend fun load(context:Context, key: String, completed:(Long)->Unit) {
        context.preferencesDatastore.data.first().run {
            completed(this[longPreferencesKey(key)] ?: 0L)
        }
    }

}