package com.yang.simpleplayer.repositories

import android.content.Context
import com.yang.simpleplayer.data.PreferencesDatastore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PreferencesDatastoreRepository {

    fun save(context:Context, key:String, value:Long) {
        GlobalScope.launch(Dispatchers.IO) {
            PreferencesDatastore.save(context, key, value)
        }
    }

    fun load(context: Context, key:String, completed:(Long)->Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            PreferencesDatastore.load(context, key, completed)
        }
    }
}