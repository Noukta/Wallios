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
    @Ignore val category: Category = Category.Iphone,
    @Ignore val tags: List<String> = listOf(),
    @Ignore var relevance: Int = 0
) {
    constructor(id: String) : this(id, url="")

    fun match(text: String) {
        val wordList = text.lowercase().split(" ").toMutableList()
        val wordInSingularList = mutableListOf<String>()
        wordList.forEach {
            wordInSingularList.add(it.removeSuffix("s"))
        }
        val wordSet = (wordList + wordInSingularList).distinct().toSet()
        relevance =  tags.intersect(wordSet).size
    }
}