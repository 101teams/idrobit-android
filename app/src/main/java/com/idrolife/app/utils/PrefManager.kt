package com.idrolife.app.utils

import android.content.Context

class PrefManager(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun setToken(token: String) {
        // Storing an access token in SharedPreferences
        saveData("access_token", token)
    }

    fun getToken(): String {
        return getData("access_token", "")
    }

    fun setRememberMe(flag: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("remember_me", flag)
        editor.apply()
    }

    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean("remember_me", false)
    }
}