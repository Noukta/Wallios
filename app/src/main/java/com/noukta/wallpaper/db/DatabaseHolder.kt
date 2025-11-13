package com.noukta.wallpaper.db

import android.content.Context
import com.noukta.wallpaper.BuildConfig

object DatabaseHolder {
    const val DATABASE_NAME = BuildConfig.APPLICATION_ID

    private var database: AppDatabase? = null

    fun init(applicationContext: Context) {
        database = AppDatabase.getInstance(applicationContext)
    }

    val Database: AppDatabase
        get() = database ?: error("DatabaseHolder must be initialized in Application.onCreate()")
}