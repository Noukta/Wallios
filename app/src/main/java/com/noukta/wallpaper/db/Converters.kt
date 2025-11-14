package com.noukta.wallpaper.db

import androidx.room.TypeConverter
import com.noukta.wallpaper.data.Category

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): Category {
        return try {
            Category.valueOf(value)
        } catch (e: IllegalArgumentException) {
            Category.Iphone
        }
    }
}
