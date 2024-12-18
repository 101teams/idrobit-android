package com.idrolife.app.theme

import androidx.compose.ui.graphics.Color
import com.idrolife.app.BuildConfig

val Black = Color(0xFF000000)
val BlackSoft = Color(0xFF353535)
val White = Color(0xFFFFFFFF)
val BrokenWhite = Color(0xFFFAF9F7)
val Gray = Color(0xFF616161)
val Primary = when(BuildConfig.FLAVOR) {
    "idroPro", "idroRes" -> {
        Color(0xFFB1000D)
    } "irriLife" -> {
        Color(0xFF005898)
    } else -> {
        Color(0xFF0D462E)
    }
}
val Primary2 = when(BuildConfig.FLAVOR) {
    "idroPro", "idroRes" -> {
        Color(0xFFCC1421)
    } "irriLife" -> {
        Color(0xFF2A64AA)
    } else -> {
        Color(0xFF508C46)
    }
}
val PrimaryVeryLight = Color(0xFFECEFEA)
val PrimaryLight = when(BuildConfig.FLAVOR) {
    "idroPro", "idroRes" -> {
        Color(0xFFE63946)
    } "irriLife" -> {
        Color(0xFFC0DFFC)
    } else -> {
        Color(0xFF4CAF50)
    }
}
val PrimaryLight2 = when(BuildConfig.FLAVOR) {
    "idroPro", "idroRes" -> {
        Color(0xFFBB2E2B)
    } "irriLife" -> {
        Color(0xFF2A64AA)
    } else -> {
        Color(0xFF67AC5C)
    }
}
val SplashColor = when(BuildConfig.FLAVOR) {
    "idroPro", "idroRes" -> {
        Color(0xFFCC1421)
    } "irriLife" -> {
        Color(0xFFC0DFFC)
    } else -> {
        Color(0xFF0D462E)
    }
}
val PrimarySoft = when(BuildConfig.FLAVOR) {
    "idroPro", "idroRes" -> {
        Color(0xFFEBB3B6)
    } "irriLife" -> {
        Color(0xFFC0DFFC)
    } else -> {
        Color(0xFFDFF4DD)
    }
}
val PrimaryPale = when (BuildConfig.FLAVOR) {
    "idroPro", "idroRes" -> {
        Color(0xFFF0E4E6)
    } "irriLife" -> {
        Color(0xFFC0DFFC)
    } else -> {
        Color(0xFFC8D4CF)
    }
}
val GreenLight2 = Color(0xFF67AC5C)
val GrayLight = Color(0xFF919191)
val GrayVeryLight = Color(0xFFE1E1E1)
val GrayVeryVeryLight = Color(0xFFF3F7F8)
val DefaultRed = Color(0xFFED424F)
val DefaultBlue = Color(0xFF2482E6)
val PlaceholderGray = Color(0xFF9C9C9C)
val DarkBlue = Color(0xFF234F8C)
val InputPlaceholderGray = Color(0xFFAEB4B7)