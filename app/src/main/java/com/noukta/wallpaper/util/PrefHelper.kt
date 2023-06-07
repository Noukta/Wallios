package com.noukta.wallpaper.util

import android.content.Context
import android.content.SharedPreferences
import com.google.android.ump.ConsentInformation

object PrefHelper {
    private const val consentStatus = "ConsentStatus"

    private const val prefFile = "preferences"
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(prefFile, Context.MODE_PRIVATE)
    }

    /*private fun getBoolean(key: String, defValue: Boolean) = preferences.getBoolean(key, defValue)

    private fun setBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }*/

    private fun getInt(key: String, defValue: Int = 0) = preferences.getInt(key, defValue)

    private fun setInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    /*private fun getLong(key: String, defValue: Long) = preferences.getLong(key, defValue)

    private fun setLong(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }*/

    fun setConsentStatus(value: Int) {
        setInt(consentStatus, value)
    }

    fun getConsentStatus(): Int {
        return getInt(consentStatus, ConsentInformation.ConsentStatus.UNKNOWN)
    }
}