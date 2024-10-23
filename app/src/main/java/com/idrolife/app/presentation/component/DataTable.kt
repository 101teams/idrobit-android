package com.idrolife.app.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.idrolife.app.R
import com.idrolife.app.data.api.sensor.RhsItem
import com.idrolife.app.theme.Green
import com.idrolife.app.theme.GreenVeryLight
import com.idrolife.app.theme.Manrope


@Composable
fun DataTableHeader(
    backgroundColor: Color,
    fontColor: Color,
    titles: MutableList<String>,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row() {
            titles.forEachIndexed { index, title ->
                Text(
                    title,
                    modifier = Modifier
                        .weight(1f),
                    color = fontColor,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    textAlign = if (index == 0) TextAlign.Start else TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
fun DataTableBody(
    backgroundColor: Color,
    fontColor: Color,
    data: RhsItem?,
    isLoading: Boolean,
    onClick: () -> Unit
){
    var isExpanded by remember { mutableStateOf(false) }
    val level = data?.level?.split(",")
    var dataValue = mutableListOf(data?.name ?: "-", level?.get(0) ?: "-", level?.get(1) ?: "-", level?.get(2) ?: "-", level?.get(3) ?: "-")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dataValue.forEachIndexed { index, datas ->
                Text(datas ?: "-",
                    modifier = Modifier
                        .weight(1f),
                    color = fontColor,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    textAlign = if (index == 0) TextAlign.Start else TextAlign.Center,
                )
            }

            IconButton(
                onClick = {
                    isExpanded = !isExpanded
                },
                modifier = Modifier
                    .width(12.dp)
                    .height(12.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(10.dp),
                    bitmap = ImageBitmap.imageResource(if (isExpanded) R.drawable.ic_arrow_noline_down_black else R.drawable.ic_arrow_noline_right_black),
                    contentDescription = "back button"
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(2.dp))

    AnimatedVisibility(visible = isExpanded) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    backgroundColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier
                ) {
                    Text(
                        stringResource(id = R.string.coordinate),
                        modifier = Modifier,
                        color = fontColor,
                        fontWeight = FontWeight.Normal,
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                    )
                    Text("${data?.latitude ?: "-"}, ${data?.longitude ?: "-"}",
                        modifier = Modifier,
                        color = fontColor,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                    )
                }

                Button(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .height(36.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = {
                          onClick()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = GreenVeryLight,),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Green,
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .width(12.dp)
                                .height(12.dp)
                        )
                    } else {
                        Text(stringResource(id = R.string.set_marker), style = MaterialTheme.typography.button, fontSize = 14.sp, color = Green)
                    }
                }
            }
        }
    }
}