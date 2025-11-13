package com.noukta.wallpaper.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noukta.wallpaper.db.obj.Wallpaper
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<Wallpaper>>

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun findById(id: String): Wallpaper?

    @Query("SELECT EXISTS (SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg wallpaper: Wallpaper)

    @Delete
    suspend fun delete(wallpaper: Wallpaper)

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()
}