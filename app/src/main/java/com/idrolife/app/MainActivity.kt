package com.idrolife.app

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.idrolife.app.navigation.Navigation
import com.idrolife.app.theme.AppTheme
import com.idrolife.app.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefManager = PrefManager(this)
        val tag = prefManager.getCurrentLanguage()
        val appLocale = LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(appLocale)

        setContent {
            AppTheme {
                Navigation()
            }
        }
    }
}

fun Context.findActivity() : Activity? = when(this){
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}