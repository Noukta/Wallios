package com.noukta.wallpaper

import android.app.Application
import com.noukta.wallpaper.admob.AdmobHelper
import com.noukta.wallpaper.util.ImageHelper
import com.noukta.wallpaper.util.PrefHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize utility singletons
        // Note: Database is managed through Hilt DI (see AppModule)
        PrefHelper.init(this)
        AdmobHelper.init(this)
        ImageHelper.init(this)
    }
}