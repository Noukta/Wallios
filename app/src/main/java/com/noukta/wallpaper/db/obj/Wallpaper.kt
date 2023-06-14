package com.noukta.wallpaper.db.obj

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.noukta.wallpaper.data.Category

@Entity(
    tableName = "favorites"
)
data class Wallpaper(
    @PrimaryKey val id: String,
    @Ignore val url: String,
    @Ignore val categories: List<Category> = listOf()
) {
    constructor(id: String) : this(id, "", listOf())
}