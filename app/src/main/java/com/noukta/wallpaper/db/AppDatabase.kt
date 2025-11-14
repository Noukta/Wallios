package com.noukta.wallpaper.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.noukta.wallpaper.db.dao.FavoritesDao
import com.noukta.wallpaper.db.obj.Wallpaper

@Database(
    version = 3,
    entities = [
        Wallpaper::class
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns for url, category, and tags
                database.execSQL("ALTER TABLE favorites ADD COLUMN url TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE favorites ADD COLUMN category TEXT NOT NULL DEFAULT 'Iphone'")
                database.execSQL("ALTER TABLE favorites ADD COLUMN tags TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, DatabaseHolder.DATABASE_NAME
            )
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()
    }
}