package com.noukta.wallpaper

import android.app.Application
import com.noukta.wallpaper.admob.AdmobHelper
import com.noukta.wallpaper.db.DatabaseHolder
import com.noukta.wallpaper.util.PrefHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseHolder().create(this)
        PrefHelper.init(this)
        AdmobHelper.init(this)
    }
}