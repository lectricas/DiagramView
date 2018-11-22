package com.example.apolusov.kotlintest

import android.app.Application
import timber.log.Timber

class App: Application() {

    companion object {
        var scaleFactor = 1f
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}