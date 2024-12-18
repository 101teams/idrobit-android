package com.idrolife.app.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idrolife.app.BuildConfig
import com.idrolife.app.R
import com.idrolife.app.data.api.device.DevicesItem
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.GreenLight2
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import kotlinx.coroutines.launch


@Composable
fun DataFieldHorizontal(label: String, value: String) {
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .background(
                GrayVeryVeryLight,
                shape = RoundedCornerShape(8.dp)
            ),
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, fontSize = 14.sp, color = Black, fontFamily = Manrope, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 16.sp, color = Black, fontFamily = Manrope, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DeviceStatusCard(
    data: DevicesItem?,
    onClick: () -> Unit,
    showDeviceError: MutableState<Boolean>,
    selectedDeviceCode: MutableState<String>,
) {
    if (data != null) {

        val scope = rememberCoroutineScope()
        val viewModel = hiltViewModel<DeviceViewModel>()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            elevation = 4.dp,
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_device),
                        contentDescription = "Center Image",
                        modifier = Modifier
                            .height(40.dp)
                            .width(40.dp),
                        contentScale = ContentScale.Fit,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (data.status?.lowercase() == "online") GreenLight2 else Color.Red)
                            )
                            Text(text = data.name ?: "-", fontSize = 20.sp, color = Color.Black, fontFamily = Manrope, fontWeight = FontWeight.SemiBold)
                        }
                        Text(text = data.code ?: "-", fontSize = 12.sp, color = Color.Gray, fontFamily = Manrope, fontWeight = FontWeight.Normal)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Alert Icon Placeholder
                    if (data.isAlarmDevice != null && data.isAlarmDevice.toInt() > 0) {
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

                // Last Heard
                Text(
                    text = "${stringResource(id = R.string.last_heard)} ${data.responseDateTime}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal
                )

                // Data fields
                DataFieldHorizontal(label = stringResource(id = R.string.system_pressure), value = data.systemPressure ?: "-")
                DataFieldHorizontal(label = stringResource(id = R.string.instant_consumption), value = data.consumption.toString())
                DataFieldHorizontal(label = stringResource(id = R.string.flow), value = data.flow ?: "-")

                Spacer(modifier = Modifier.height(4.dp))

                // Active Programs and Stations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                GrayVeryVeryLight,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                            .weight(1f),
                    ) {
                        Column(
                            modifier = Modifier,
                        ) {
                            Text(text = stringResource(id = R.string.active_program), fontSize = 14.sp, color = Black, fontFamily = Manrope, fontWeight = FontWeight.Medium)
                            Text(text = data.activeProgram ?: "-", fontSize = 16.sp, color = Black, fontFamily = Manrope, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                GrayVeryVeryLight,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                            .weight(1f),
                    ) {
                        Column(
                            modifier = Modifier,
                        ) {
                            Text(text = stringResource(id = R.string.active_station), fontSize = 14.sp, color = Black, fontFamily = Manrope, fontWeight = FontWeight.Medium)
                            Text(text = data.activeStation ?: "-", fontSize = 16.sp, color = Black, fontFamily = Manrope, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                if (BuildConfig.FLAVOR == "idroPro") {

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                GrayVeryVeryLight,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = stringResource(id = R.string.fast_watering), fontSize = 14.sp, color = Black, fontFamily = Manrope, fontWeight = FontWeight.Medium)

                            SmallToggleWithTitle(
                                field = null,
                                checkedTitle = "ON",
                                uncheckedTitle = "OFF",
                                modifier = Modifier.width(70.dp),
                                selectedValue = data.isFastWatering == true,
                                onChecked = {checked ->
                                    scope.launch {
                                        viewModel.postFastWatering(data.code ?: "", checked)
                                    }
                                }
                            )
                        }
                    }
                }

                if (data.isAlarmDevice != null && data.isAlarmDevice.toInt() > 0) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(62.dp)
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            selectedDeviceCode.value = data.code ?: ""
                            showDeviceError.value = true
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                        border = BorderStroke(1.dp, Color.Red),
                    ) {
                        Text(stringResource(id = R.string.see_alarms), style = MaterialTheme.typography.button, fontSize = 18.sp, color = Color.Red)
                    }
                }
            }
        }
    }
}