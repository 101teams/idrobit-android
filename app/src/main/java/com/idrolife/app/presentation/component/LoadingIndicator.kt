package com.idrolife.app.presentation.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.idrolife.app.theme.Black

@Composable
fun LoadingIndicator(size: Dp = 18.dp, color: Color = Black) {
    CircularProgressIndicator(
        color = color,
        backgroundColor = Black.copy(alpha = 0.3f),
        strokeCap = StrokeCap.Round,
        strokeWidth = 2.dp,
        modifier = Modifier
            .width(size)
            .height(size)
    )
}