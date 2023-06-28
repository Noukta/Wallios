package com.noukta.wallpaper.settings

object ReviewTime {

    const val MIN_INTERVAL = 300_000L
    const val MAX_INTERVAL = 600_000L
}

enum class ReviewChoice{
    NOT_ENJOY, RATE, REMIND
}