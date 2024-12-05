package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
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
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.PrimaryLight2
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper

@Composable
fun FertigationStatusScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()

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

        viewModel.getFertigationStatus(deviceCode)

        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevicesByID(deviceID)
    }

    LaunchedEffect(Unit) {
        viewModel.startPeriodicFetchingFertigationStatus(deviceCode)
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
            stringResource(id = R.string.fertigation_status),
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
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp,
                    backgroundColor = White,
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    stringResource(id = R.string.counter_principal),
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    viewModel.fertigationStatus.value?.counterPrincipal ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    stringResource(id = R.string.number_of_alarm),
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    viewModel.fertigationStatus.value?.numberOfAlarm ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    "${stringResource(id = R.string.counter)} 1",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    viewModel.fertigationStatus.value?.counter1 ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    "${stringResource(id = R.string.counter)} 2",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    viewModel.fertigationStatus.value?.counter2 ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    "${stringResource(id = R.string.counter)} 3",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    viewModel.fertigationStatus.value?.counter3 ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    "${stringResource(id = R.string.counter)} 4",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    viewModel.fertigationStatus.value?.counter4 ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    "EC",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    viewModel.fertigationStatus.value?.ec ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    "pH",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    viewModel.fertigationStatus.value?.ph ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp,
                    backgroundColor = White,
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        columns = GridCells.Fixed(2),
                    ) {
                        items(8) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "${stringResource(id = R.string.program)} ${it + 1}",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(if((viewModel.fertigationStatus.value?.activeProgram ?: "").contains("${it+1}")) PrimaryLight2 else Color.Red)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
