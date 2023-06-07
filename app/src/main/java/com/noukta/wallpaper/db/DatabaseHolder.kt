package com.noukta.wallpaper.db

import android.content.Context
import androidx.room.Room
import com.noukta.wallpaper.BuildConfig

class DatabaseHolder {
    fun create(applicationContext: Context) {
        Database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID
        lateinit var Database: AppDatabase
    }
}