package com.idrolife.app.utils

import android.content.Context
import com.google.gson.Gson
import com.idrolife.app.data.api.auth.User

enum class NetworkType {
    WIFI,
    MOBILE_DATA,
    UNKNOWN
}

data class OriginalNetworkInfo(
    val ssid: String?,
    val networkType: NetworkType,
    val signalStrength: Int?,
    val securityType: String?,
    val frequency: Int?,
    val bssid: String?,
    val timestamp: Long = System.currentTimeMillis()
)

enum class RestorationStep {
    DISCONNECTING_CURRENT,
    REMOVING_TEMPORARY,
    CONNECTING_TO_ORIGINAL,
    WAITING_FOR_CONNECTION,
    VERIFYING_INTERNET,
    COMPLETED,
    FAILED
}

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

    fun setServerData(data: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ServerDataKey, data)
        editor.apply()
    }

    fun getServerData(): String? {
        return sharedPreferences.getString(ServerDataKey, null)
    }

    fun setPreviousWifi(ssid: String?) {
        saveData("previous_wifi_ssid", ssid ?: "")
    }

    fun getPreviousWifi(): String? {
        val ssid = getData("previous_wifi_ssid", "")
        return if (ssid.isEmpty()) null else ssid
    }

    fun saveOriginalNetworkInfo(networkInfo: OriginalNetworkInfo) {
        val json = Gson().toJson(networkInfo)
        saveData("original_network_info", json)
    }

    fun getOriginalNetworkInfo(): OriginalNetworkInfo? {
        val json = getData("original_network_info", "")
        return if (json.isEmpty()) {
            null
        } else {
            try {
                Gson().fromJson(json, OriginalNetworkInfo::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun clearOriginalNetworkInfo() {
        val editor = sharedPreferences.edit()
        editor.remove("original_network_info")
        editor.apply()
    }

    fun getLastRestorationAttempt(): Long {
        return sharedPreferences.getLong("last_restoration_attempt", 0)
    }

    fun setLastRestorationAttempt(timestamp: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong("last_restoration_attempt", timestamp)
        editor.apply()
    }
}