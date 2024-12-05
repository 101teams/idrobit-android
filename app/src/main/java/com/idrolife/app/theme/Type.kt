package com.idrolife.app.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.idrolife.app.R

val Manrope = FontFamily(
    Font(R.font.manrope_regular),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_light, FontWeight.Thin),
    Font(R.font.manrope_extra_bold, FontWeight.ExtraBold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 96.sp,
        color = Primary,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    h4 = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp,
        color = Primary,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    subtitle1 = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = Primary
    ),
    body1 = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Primary
    ),
    body2 = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = Primary
    ),
    caption = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        color = Primary
    ),
    button = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = White
    )
)