package com.noukta.wallpaper.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.noukta.wallpaper.db.dao.FavoritesDao
import com.noukta.wallpaper.db.obj.Wallpaper

@Database(
    version = 1,
    entities = [
        Wallpaper::class
    ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
}