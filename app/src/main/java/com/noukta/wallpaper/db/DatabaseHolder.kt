package com.noukta.wallpaper.db

import android.content.Context
import com.noukta.wallpaper.BuildConfig

object DatabaseHolder {
    const val DATABASE_NAME = BuildConfig.APPLICATION_ID

    private var instance: AppDatabase? = null

    fun init(applicationContext: Context) {
        instance = AppDatabase.getInstance(applicationContext)
    }

    val database: AppDatabase
        get() = instance ?: error("DatabaseHolder must be initialized in Application.onCreate()")
}