package com.yang.simpleplayer.fragments.preference

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.yang.simpleplayer.R

class ControllerThemaPreferFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.controller_thema_preference, rootKey)
    }
}