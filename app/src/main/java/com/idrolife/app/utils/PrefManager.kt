package com.idrolife.app.utils

import android.content.Context
import com.google.gson.Gson
import com.idrolife.app.data.api.auth.User

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

    fun setCurrentLanguage(language: String) {
        val editor = sharedPreferences.edit()
        editor.putString("language", language)
        editor.apply()
    }

    fun getUser(): User? {
        val userJSON = sharedPreferences.getString("user", null)
        return if (userJSON != null) {
            Gson().fromJson(userJSON, User::class.java)
        } else {
            null
        }
    }

    fun setUser(user: User?) {
        val editor = sharedPreferences.edit()
        val userJson = Gson().toJson(user)
        editor.putString("user", userJson)
        editor.apply()
    }

    fun getCurrentLanguage(): String {
        return sharedPreferences.getString("language", "it")!!
    }

    fun setRememberMe(flag: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("remember_me", flag)
        editor.apply()
    }

    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean("remember_me", false)
    }

    private val ServerDataKey = "server_data"

    fun saveServerData(data: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ServerDataKey, data)
        editor.apply()
    }

    fun getServerData(): String? {
        return sharedPreferences.getString(ServerDataKey, null)
    }
}