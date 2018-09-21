package com.onelio.connectu.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class Settings(context: Context) {

    /**
     * Private preference context
     */
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Returns an int from the preferences.
     * -1 if not valid.
     */
    fun getInt(name: String): Int {
        return preferences.getInt(name, -1)
    }

    /**
     * Returns a long from the preferences.
     * -1 if not valid.
     */
    fun getLong(name: String): Long {
        return preferences.getLong(name, -1)
    }

    /**
     * Returns a bool from the preferences.
     * false if not valid.
     */
    fun getBoolean(name: String): Boolean {
        return preferences.getBoolean(name, false)
    }

    /**
     * Returns a string from the preferences.
     * "" if not valid.
     */
    fun getString(name: String): String {
        return preferences.getString(name, "")
    }

    /**
     * Puts a long in the preferences based on a key
     */
    fun putLong(name: String, value: Long) {
        val editor = preferences.edit()
        editor.putLong(name, value)
        editor.apply()
    }

    /**
     * Puts an int in the preferences based on a key
     */
    fun putInt(name: String, value: Int) {
        val editor = preferences.edit()
        editor.putInt(name, value)
        editor.apply()
    }

    /**
     * Puts a bool in the preferences based on a key
     */
    fun putBoolean(name: String, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(name, value)
        editor.apply()
    }

    /**
     * Puts a string in the preferences based on a key
     */
    fun putString(name: String, value: String) {
        val editor = preferences.edit()
        editor.putString(name, value)
        editor.apply()
    }

    /**
     * Deletes all preferences
     */
    fun deleteAll() {
        preferences.edit().clear().apply()
    }
}