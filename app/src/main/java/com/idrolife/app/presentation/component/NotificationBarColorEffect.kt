package com.idrolife.app.presentation.component

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.utils.Helper

@Composable
fun NotificationBarColorEffect(
    color: Int = BrokenWhite.toArgb(),
    isDarkIcons: Boolean = true
) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return
    val window = activity.window
    val view = LocalView.current

    val helper = remember { Helper() }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                helper.setNotifBarColor(view, window, color, isDarkIcons)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}