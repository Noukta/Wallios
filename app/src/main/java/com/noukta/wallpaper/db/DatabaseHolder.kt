package com.noukta.wallpaper.db

import android.content.Context
import com.noukta.wallpaper.BuildConfig

class DatabaseHolder {
    fun create(applicationContext: Context) {
        Database = AppDatabase.getInstance(applicationContext)
    }

    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID
        lateinit var Database: AppDatabase
    }
}