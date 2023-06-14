package com.noukta.wallpaper.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noukta.wallpaper.db.obj.Wallpaper

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites")
    fun getAll(): List<Wallpaper>

    @Query("SELECT * FROM favorites WHERE id = :id")
    fun findById(id: Int): Wallpaper

    @Query("SELECT EXISTS (SELECT 1 FROM favorites WHERE id = :id)")
    fun exists(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg wallpaper: Wallpaper)

    @Delete
    fun delete(wallpaper: Wallpaper)

    @Query("DELETE FROM favorites")
    fun deleteAll()
}