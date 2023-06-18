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
    @Ignore val category: Category,
    @Ignore val tags: List<String> = listOf()
) {
    constructor(id: String) : this(id, "",Category.Iphone, listOf())

    fun match(tag: String): Boolean {
        val tagVariants = listOf(
            tag.replace(" ", "").lowercase()
        ) + tag.lowercase().split(" ")
        return tags.any {
            it in tagVariants
        }
    }
}