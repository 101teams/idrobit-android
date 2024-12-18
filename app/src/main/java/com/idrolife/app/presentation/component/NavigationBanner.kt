package com.idrolife.app.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.data.api.device.DevicesItem
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GreenLight2
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.PrimaryLight2
import com.idrolife.app.theme.White


@Composable
fun NavigationBanner1(
    navController: NavController,
    title: String,
    desc: String,
    icon: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryLight2)
            .height(180.dp)
    ) {
        Column {
            CustomTopBar(title = "", navController, null, false)
            Spacer(modifier = Modifier.height(12.dp))
            Text(title,
                modifier = Modifier
                    .padding(start = 28.dp),
                fontSize = 18.sp,
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                color = White,
            )
            Text(desc,
                modifier = Modifier
                    .padding(start = 28.dp),
                fontSize = 30.sp,
                fontFamily = Manrope,
                fontWeight = FontWeight.ExtraBold,
                color = White,
            )
        }
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Center Image",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .height(84.dp)
                .width(99.dp),
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Composable
fun NavigationBanner2(
    navController: NavController,
    title: String,
    icon: Int,
    data: DevicesItem?,
    isLoading: Boolean?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryLight2)
            .height(180.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Center Image",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .height(84.dp)
                .width(99.dp),
            contentScale = ContentScale.FillBounds,
        )

        Column {
            Column(
                modifier = Modifier.padding(top=24.dp)
            ) {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    navController.navigateUp()
                                },
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(36.dp)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(24.dp),
                                    bitmap = ImageBitmap.imageResource(R.drawable.ic_arrow_back_white),
                                    contentDescription = "back button"
                                )
                            }

                            Text(
                                title,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = White,
                                    fontFamily = Manrope,
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp,
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    if (isLoading != null && !isLoading) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                            ) {
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically,
                                ){
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(if (data?.status?.lowercase() == "online") GreenLight2 else Color.Red)
                                    )
                                    Text(text = data?.name ?: "-", fontSize = 20.sp, color = Color.Black, fontFamily = Manrope, fontWeight = FontWeight.SemiBold)
                                }
                                Text(text = data?.code ?: "", fontSize = 12.sp, color = Color.Gray, fontFamily = Manrope, fontWeight = FontWeight.Normal)
                            }

                            // Alert Icon Placeholder
                            if (data?.isAlarmDevice != null && data.isAlarmDevice.toIntOrNull() != null && data.isAlarmDevice.toInt() > 0) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_alert_red),
                                    contentDescription = "Center Image",
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(24.dp),
                                    contentScale = ContentScale.Fit,
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                color = Primary,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(18.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationBanner3(
    navController: NavController,
    title: String,
    icon: Int,
    data: DevicesItem?,
    isLoading: Boolean?,
) {
    Column(
        modifier = Modifier
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        },
                        modifier = Modifier
                            .width(36.dp)
                            .height(36.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(28.dp),
                            bitmap = ImageBitmap.imageResource(R.drawable.ic_arrow_back_black),
                            contentDescription = "back button"
                        )
                    }

                    Text(
                        title,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Black,
                            fontFamily = Manrope,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            },
            backgroundColor = BrokenWhite,
            elevation = 2.dp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top=12.dp, start = 24.dp, end = 24.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = 4.dp,
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            if (isLoading != null && !isLoading) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (data?.status?.lowercase() == "online") GreenLight2 else Color.Red)
                            )
                            Text(text = data?.name ?: "-", fontSize = 20.sp, color = Color.Black, fontFamily = Manrope, fontWeight = FontWeight.SemiBold)
                        }
                        Text(text = data?.code ?: "", fontSize = 12.sp, color = Color.Gray, fontFamily = Manrope, fontWeight = FontWeight.Normal)
                    }

                    // Alert Icon Placeholder
                    if (data?.isAlarmDevice != null && data.isAlarmDevice.toIntOrNull() != null && data.isAlarmDevice.toInt() > 0) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_alert_red),
                            contentDescription = "Center Image",
                            modifier = Modifier
                                .height(24.dp)
                                .width(24.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp)
                ) {
                    CircularProgressIndicator(
                        color = Primary,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}