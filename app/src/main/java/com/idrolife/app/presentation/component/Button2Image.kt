package com.idrolife.app.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.idrolife.app.theme.White


@Composable
fun Button2Image(
    backgroundColor: Color,
    leftImage: Int,
    title: String,
    rightImage: Int,
    onClick: () -> Unit,
    fontColor: Color?,
    outlineColor: Color?,
){
    Button(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .height(62.dp)
            .fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        border = BorderStroke(1.dp, outlineColor ?: Color.Transparent),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = leftImage),
                    contentDescription = "",
                    modifier = Modifier
                        .height(36.dp)
                        .width(36.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.button, fontSize = 16.sp, color = fontColor ?: White, fontWeight = FontWeight.Medium,)
            }
            Image(
                painter = painterResource(id = rightImage),
                contentDescription = "",
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
fun Button2ImageOutlined(
    backgroundColor: Color,
    leftImage: Int,
    title: String,
    rightImage: Int,
    onClick: () -> Unit,
){
    Button(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .height(62.dp)
            .fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = leftImage),
                    contentDescription = "",
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.button, fontSize = 18.sp)
            }
            Image(
                painter = painterResource(id = rightImage),
                contentDescription = "",
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}