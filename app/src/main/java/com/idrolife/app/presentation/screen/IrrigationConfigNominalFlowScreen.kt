package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.data.api.irrigation.IrrigationConfigNominalFlowDataProduct
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BlackSoft
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Gray
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.GrayVeryLight
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Green
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@Composable
fun IrrigationConfigNominalFlowScreen(
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

        viewModel.getIrrigationConfigNominalFlow(viewModel.selectedDevice.value?.code ?: "")
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
            stringResource(id = R.string.nominal_flow),
            R.drawable.img_header_detail3,
            viewModel.selectedDevice.value,
            viewModel.isLoading.value,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(viewModel.irrigationConfigNominalFlow.value.size) {index ->
                var item = viewModel.irrigationConfigNominalFlow.value[index]

                if (item.evSerial != "FFFFFF") {
                    try {
                        if (item.nominalValue.toInt() >= 6000) {
                            item.auto = true
                        }
                    } catch (e: Exception) {}

                    NominalFlowItem(item,
                        onAuto = {
                            viewModel.irrigationConfigNominalFlow.value[index].auto = it
                            if (it) {
                                viewModel.irrigationConfigNominalFlow.value[index].nominalValue = "6000"
                            }
                        },
                        onValueChanged = {
                            viewModel.irrigationConfigNominalFlow.value[index].nominalValue = it
                        }
                    )
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
                        val data = mutableMapOf<String, String>()

                        var evIndex = 2005

                        for (i in viewModel.irrigationConfigNominalFlow.value) {
                            data["S${evIndex}"] = if (i.nominalValue == "") {
                                "0"
                            } else {
                                i.nominalValue
                            }

                            evIndex += 6
                        }

                        val chunkItemSize = 60
                        var sendData = mutableMapOf<String, String>()
                        data.onEachIndexed { index, entry ->
                            sendData[entry.key] = entry.value

                            if ((index + 1) % chunkItemSize == 0 || index+1 == data.size) {
                                val resp = viewModel.postIrrigationConfigNominalFlow(
                                    viewModel.selectedDevice.value?.code ?: "",
                                    sendData,
                                )
                                sendData = mutableMapOf()
                            }
                        }

                        viewModel.postDataLoading.value = false
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Green),
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
                    Text(stringResource(id = R.string.save), style = MaterialTheme.typography.button, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun NominalFlowItem(
    data: IrrigationConfigNominalFlowDataProduct,
    onAuto: (Boolean) -> Unit,
    onValueChanged: (String) -> Unit,
) {
    val nominalValue = remember { mutableStateOf(data.nominalValue) }
    val modeAuto = remember { mutableStateOf(data.auto) }

    val switchPadding by animateDpAsState(targetValue = if (modeAuto.value) 70.dp else 0.dp)
    val switchRightPaddingText by animateDpAsState(targetValue = if (modeAuto.value) 8.dp else 0.dp)
    val switchLeftPaddingText by animateDpAsState(targetValue = if (modeAuto.value) 0.dp else 12.dp)

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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("EV Serial",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = GrayLight,
                        ),
                    )
                    Text(data.evSerial ?: "",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = Black,
                        ),
                    )
                }
                Column {
                    Text("Station",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = GrayLight,
                        ),
                    )
                    Text(data.station ?: "",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = Black,
                        ),
                    )
                }
                Column {
                    Text("Pump",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = GrayLight,
                        ),
                    )
                    Text(data.pump ?: "",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = Black,
                        ),
                    )
                }
                Column {
                    Text("Master V.",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = GrayLight,
                        ),
                    )
                    Text(data.master ?: "",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = Black,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Mode",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = GrayLight,
                        ),
                    )

                    //switch
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(40.dp)
                            .background(
                                color = if (modeAuto.value) Green else BlackSoft,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(4.dp)
                            .clickable {
                                modeAuto.value = !modeAuto.value
                                onAuto(modeAuto.value)
                           },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = if (modeAuto.value) "Auto" else "Manual",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Left,
                                color = White,
                            ),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(
                                    start = switchLeftPaddingText,
                                    end = switchRightPaddingText
                                )
                        )

                        Box(
                            modifier = Modifier
                                .padding(start = switchPadding)
                                .size(30.dp)
                                .background(color = Color.White, shape = CircleShape)
                        )
                    }
                }
                Column {
                    Text("Nominal Value",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left,
                            color = GrayLight,
                        ),
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                    ){
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(100.dp)
                                .background(
                                    if (modeAuto.value) Color(0xFFF2F2F2) else GrayVeryLight,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                BasicTextField(
                                    value = nominalValue.value,
                                    onValueChange = {
                                        nominalValue.value = it
                                        onValueChanged(nominalValue.value)
                                    },
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Left,
                                        color = if (modeAuto.value) GrayLight else Black,
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (nominalValue.value.isEmpty()) {
                                            Text(
                                                text = "",
                                                color = Gray,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                fontFamily = Manrope,
                                                textAlign = TextAlign.Left,
                                            )
                                        }
                                        innerTextField()
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Number,
                                    ),
                                    enabled = !modeAuto.value
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("m³/h")
                    }
                }
            }
        }
    }
}
