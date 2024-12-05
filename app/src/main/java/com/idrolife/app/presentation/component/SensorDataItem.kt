package com.idrolife.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2


@Composable
fun SensorDataItem(
    value: String,
    unit: String,
    name: String,
    modifier: Modifier,
){
    Box(
        modifier = modifier.then(
            Modifier
                .background(
                    GrayVeryVeryLight,
                    shape = RoundedCornerShape(8.dp)
                )
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = value, fontSize = 34.sp, fontFamily = Manrope, fontWeight = FontWeight.Normal, color = Primary,)
                Text(text = unit, fontSize = 18.sp, fontFamily = Manrope, fontWeight = FontWeight.Normal, color = Primary,)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = name, fontSize = 14.sp, fontFamily = Manrope, fontWeight = FontWeight.Medium, color = Primary2,)
        }
    }
}