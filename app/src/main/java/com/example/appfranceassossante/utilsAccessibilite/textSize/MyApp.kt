package com.example.appfranceassossante.utilsAccessibilite.textSize

import android.app.Application

class MyApp : Application() {
    companion object {
        lateinit var instance: MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        TextSizeManager.init(this)
    }
}