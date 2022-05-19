package com.yang.simpleplayer.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.yang.simpleplayer.Preferences.thema.ControllerThema
import com.yang.simpleplayer.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferencesRepository (private val userPreferencesStore: DataStore<Preferences>) {
    object PreferencesKeys {
        val CONTROLLER_THEMA_KEY = intPreferencesKey("controller_thema_key")
    }
    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data
        .map { preferences ->
            val comtrollerThema = preferences[PreferencesKeys.CONTROLLER_THEMA_KEY] ?: ControllerThema.BUTTON.ordinal
            UserPreferences(comtrollerThema)
        }

    suspend fun getFirstUserPreferences() = userPreferencesFlow.first()

    suspend fun updateControllerThema(controllerThema:Int) {
        userPreferencesStore.edit { preferences ->
            preferences[PreferencesKeys.CONTROLLER_THEMA_KEY] = controllerThema
        }
    }
}