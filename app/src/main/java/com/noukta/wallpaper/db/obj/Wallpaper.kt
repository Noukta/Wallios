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
    val url: String = "",
    val category: Category = Category.Iphone,
    val tags: List<String> = listOf(),
    @Ignore var relevance: Int = 0
) {
    fun match(text: String): Wallpaper {
        val query = text.lowercase().trim()
        if (query.isEmpty()) {
            return this.copy().apply { relevance = 0 }
        }

        val words = query.split("\\s+".toRegex())
        var score = 0

        tags.forEach { tag ->
            val tagLower = tag.lowercase()

            // Exact match gets highest score
            if (tagLower == query) {
                score += 100
            }
            // Exact word match (tag equals any query word)
            else if (words.any { it == tagLower }) {
                score += 50
            }
            // Tag contains the full query
            else if (tagLower.contains(query)) {
                score += 25
            }
            // Tag contains any individual query word
            else if (words.any { word -> tagLower.contains(word) && word.length > 2 }) {
                score += 10
            }
            // Fuzzy match: check for singular/plural variations
            else {
                words.forEach { word ->
                    val singular = word.removeSuffix("s")
                    val plural = if (word.endsWith("s")) word else word + "s"
                    when {
                        tagLower == singular || tagLower == plural -> score += 40
                        tagLower.contains(singular) || tagLower.contains(plural) -> score += 8
                    }
                }
            }
        }

        return this.copy().apply { relevance = score }
    }
}