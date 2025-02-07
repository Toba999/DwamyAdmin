package com.dev.dwamyadmin

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class MyApplication : Application() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(forceLeftToRight(newBase))
    }

    private fun forceLeftToRight(context: Context): Context {
        val config = Configuration(context.resources.configuration)
        config.setLayoutDirection(Locale.ENGLISH)
        return context.createConfigurationContext(config)
    }
}
