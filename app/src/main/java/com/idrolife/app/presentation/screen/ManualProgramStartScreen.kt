package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.idrolife.app.presentation.component.SmallToggleWithTitle
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@Composable
fun ManualProgramStartScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String
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

        viewModel.getManualStartProgram(deviceCode, context)

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
            stringResource(id = R.string.ev_configuration),
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
            LazyVerticalGrid(
                modifier = Modifier.padding(horizontal = 16.dp),
                columns = GridCells.Fixed(4),
            ) {
                item(span = { GridItemSpan(4) }) { Spacer(modifier = Modifier.height(24.dp)) }
                itemsIndexed(viewModel.manualStartProgram.value) { index, it ->
                    Box(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = 4.dp,
                            backgroundColor = GrayVeryVeryLight,
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    it.name ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                SmallToggleWithTitle(
                                    field = null,
                                    checkedTitle = "ON",
                                    uncheckedTitle = "OFF",
                                    modifier = Modifier,
                                    selectedValue = it.value == "1",
                                    onChecked = {checked ->
                                        scope.launch {
                                            val sendData = mutableMapOf<String, String>()
                                            if (checked) {
                                                sendData["S997"] = "${index + 1}"
                                            } else {
                                                sendData["S999"] = "${index + 1}"
                                            }
                                            viewModel.postManualStartProgram(deviceCode, sendData)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(4) }) { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}