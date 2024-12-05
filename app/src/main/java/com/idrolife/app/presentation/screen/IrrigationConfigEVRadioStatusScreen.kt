package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.data.api.irrigation.IrrigationConfigEVRadioStatus
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@Composable
fun IrrigationConfigEVRadioStatusScreen(
    navController: NavController,
    deviceID: String
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()
    val scope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Helper().setNotifBarColor(view, window, BrokenWhite.toArgb(),true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isLoading.value = true

        val result = viewModel.getDeviceByID(deviceID)

        if (result.second == "Unauthorized") {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        } else if (result.second.isNotBlank() && !result.second.contains("coroutine scope")) {
            Toast.makeText(context, result.second, Toast.LENGTH_LONG)
                .show()
        }

        viewModel.getIrrigationConfigEvRadioStatus(viewModel.selectedDevice.value?.code ?: "")
        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevicesByID(deviceID)
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        NavigationBanner3(
            navController,
            stringResource(id = R.string.ev_radio_status),
            R.drawable.img_header_detail3,
            viewModel.selectedDevice.value,
            viewModel.isLoading.value,
        )

        if (viewModel.isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    color = Primary,
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(viewModel.irrigationConfigEVRadioStatus.value) {
                    EVRadioStatusItem(it)
                }
            }
        }

        Column(
            modifier = Modifier
                .background(White)
                .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .height(62.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                      scope.launch {
                          viewModel.postDataLoading.value = true
                          viewModel.isLoading.value = true

                          viewModel.postIrrigationConfigEVRadioStatusRefresh(
                              deviceCode = viewModel.selectedDevice.value?.code ?: "",
                          )
                          viewModel.getIrrigationConfigAdvanceConfig(viewModel.selectedDevice.value?.code ?: "")

                          viewModel.isLoading.value = false
                          viewModel.postDataLoading.value = false
                      }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary2),
            ) {
                if (viewModel.postDataLoading.value) {
                    CircularProgressIndicator(
                        color = White,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp)
                    )
                } else {
                    Text(stringResource(id = R.string.refresh), style = MaterialTheme.typography.button, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun EVRadioStatusItem(
data: IrrigationConfigEVRadioStatus
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = GrayVeryVeryLight,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(data.serialID ?: "-",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Left,
                        color = Primary,
                    ),
                )
                Text("Group ${data.group ?: "-"}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right,
                        color = Primary,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_battery_green),
                        contentDescription = "Center Image",
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        contentScale = ContentScale.Fit,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(data.batteryLevel ?: "-",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = Primary2,
                        ),
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_signal_green),
                        contentDescription = "Center Image",
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        contentScale = ContentScale.Fit,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(data.signal ?: "-",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = Primary2,
                        ),
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("OK - ERR",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left,
                            color = Primary2,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${data.goodData}-${data.errorData}",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = Primary2,
                        ),
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("ERR%",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left,
                            color = Primary2,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(data.errorPercentage ?: "-",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = Primary2,
                        ),
                    )
                }
            }
        }
    }
}
