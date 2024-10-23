package com.idrolife.app.utils

import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat

class Helper {
    fun setNotifBarColor(view: View, window: Window, color: Int, darkIcon: Boolean) {
        window.statusBarColor = color

        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = darkIcon
    }
}