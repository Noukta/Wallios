package com.noukta.wallpaper.util

import android.content.Context
import android.content.SharedPreferences
import com.google.android.ump.ConsentInformation
import com.noukta.wallpaper.settings.ReviewChoice

object PrefHelper {
    private const val consentStatus = "ConsentStatus"
    private const val reviewStatus = "reviewStatus"
    private const val timeSpent = "TimeSpent"
    private const val lastPostNotificationsRequestTime = "LastPostNotificationsRequestTime"


    private const val prefFile = "preferences"
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(prefFile, Context.MODE_PRIVATE)
    }

    private fun checkInitialized() {
        check(::preferences.isInitialized) {
            "PrefHelper must be initialized by calling init(context) before use"
        }
    }

    private fun getInt(key: String, defValue: Int = 0): Int {
        checkInitialized()
        return preferences.getInt(key, defValue)
    }

    private fun setInt(key: String, value: Int) {
        checkInitialized()
        preferences.edit().putInt(key, value).apply()
    }

    private fun getLong(key: String, defValue: Long): Long {
        checkInitialized()
        return preferences.getLong(key, defValue)
    }

    private fun setLong(key: String, value: Long) {
        checkInitialized()
        preferences.edit().putLong(key, value).apply()
    }

    fun setConsentStatus(value: Int) {
        setInt(consentStatus, value)
    }

    fun getConsentStatus(): Int {
        return getInt(consentStatus, ConsentInformation.ConsentStatus.UNKNOWN)
    }

    fun setReviewStatus(reviewChoice: ReviewChoice) {
        setInt(reviewStatus, reviewChoice.ordinal)
    }

    fun getReviewStatus(): ReviewChoice {
        val ordinal = getInt(reviewStatus, ReviewChoice.REMIND.ordinal)
        return ReviewChoice.values().getOrElse(ordinal) { ReviewChoice.REMIND }
    }

    fun setTimeSpent(time: Long) {
        setLong(timeSpent, time)
    }

    fun getTimeSpent(): Long{
        return getLong(timeSpent, 0)
    }

    fun resetLastPostNotificationsRequestTime() {
        val currentTime = System.currentTimeMillis()
        setLong(lastPostNotificationsRequestTime, currentTime)
    }

    fun getLastPostNotificationsRequestTime(): Long {
        return getLong(lastPostNotificationsRequestTime, 0)
    }
}