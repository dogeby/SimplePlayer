package com.yang.simpleplayer.models

import com.yang.simpleplayer.Preferences.thema.ControllerThema

data class UserPreferences(val controllerThema: Int = ControllerThema.BUTTON.ordinal) {
}