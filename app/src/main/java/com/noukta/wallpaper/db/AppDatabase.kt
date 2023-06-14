package com.noukta.wallpaper.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.noukta.wallpaper.db.dao.FavoritesDao
import com.noukta.wallpaper.db.obj.Wallpaper

@Database(
    version = 2,
    entities = [
        Wallpaper::class
    ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, DatabaseHolder.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }
}