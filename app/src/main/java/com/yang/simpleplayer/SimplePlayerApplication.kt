package com.yang.simpleplayer

import android.app.Application

class SimplePlayerApplication : Application(){
    val appContainer by lazy { AppContainer(applicationContext) }
}