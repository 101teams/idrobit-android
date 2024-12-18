package com.idrolife.app.utils

import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class Helper {
    fun setNotifBarColor(view: View, window: Window, color: Int, darkIcon: Boolean) {
        window.statusBarColor = color

        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = darkIcon
    }

    fun dynamicFormatDate(date: String, expectedFormat: String): String? {
        val dateFormat = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
        )

        for (format in dateFormat) {
            try {
                val inputFormatter = SimpleDateFormat(format, Locale.getDefault())
                inputFormatter.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormatter.parse(date)
                val outputFormatter = SimpleDateFormat(expectedFormat, Locale.getDefault())
                outputFormatter.timeZone = TimeZone.getDefault()
                return outputFormatter.format(date!!)
            } catch (e: Exception) {

            }
        }
        return null
    }
}