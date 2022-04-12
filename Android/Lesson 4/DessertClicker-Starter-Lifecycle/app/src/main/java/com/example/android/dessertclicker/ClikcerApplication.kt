package com.example.android.dessertclicker

import android.app.Application
import timber.log.Timber

class ClikcerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Timber 초기화
        Timber.plant(Timber.DebugTree())
    }
}