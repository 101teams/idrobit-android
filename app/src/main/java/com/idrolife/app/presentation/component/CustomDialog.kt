package com.idrolife.app.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.idrolife.app.R
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.DefaultRed
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.GrayVeryLight
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TopToastDialog(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    durationMillis: Long = 2000L
) {
    if (isVisible) {
        LaunchedEffect(Unit) {
            delay(durationMillis)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF333333),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(start = 4.dp, top = 4.dp, bottom = 4.dp, end = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_idrolife),
                        contentDescription = "Center Image",
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        contentScale = ContentScale.Fit,
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun DialogCancelCreatePlant(
    onDismiss: () -> Unit,
    onClickContinue: () -> Unit,
    onClickCancel: () -> Unit,
){
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = Modifier
                .background(
                    BrokenWhite,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(18.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_alert_red),
                    contentDescription = "Center Image",
                    modifier = Modifier
                        .height(36.dp)
                        .width(36.dp),
                    contentScale = ContentScale.Fit,
                )

                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(id = R.string.are_you_sure),
                    color = GrayLight,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                )

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResource(id = R.string.entered_data_will_lost),
                    color = GrayLight,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ){

                    Button(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(62.dp)
                            .weight(1f)
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            onClickCancel()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = DefaultRed,),
                    ) {
                        Text(stringResource(id = R.string.cancel), style = MaterialTheme.typography.button, fontSize = 18.sp, color = White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(62.dp)
                            .weight(1f)
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            onClickContinue()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                        border = BorderStroke(1.dp, DefaultRed),
                    ) {
                        Text(stringResource(id = R.string.continued), style = MaterialTheme.typography.button, fontSize = 18.sp, color = DefaultRed)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogEditPlant(
    onDismiss: () -> Unit,
    onClickContinue: () -> Unit,
    onClickCancel: () -> Unit,
    name: MutableState<String>,
    deviceList: MutableList<Pair<String,String>>,
    onSelectedDevice: (String) -> Unit,
    isLoading: MutableState<Boolean>,
){
    val selectedEditDevice = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = Modifier
                .background(
                    BrokenWhite,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(18.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_edit_green),
                    contentDescription = "Center Image",
                    modifier = Modifier
                        .height(36.dp)
                        .width(36.dp),
                    contentScale = ContentScale.Fit,
                )

                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(id = R.string.edit_plant).uppercase(),
                    color = GrayLight,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                )

                DropDown(
                    field = null,
                    deviceList,
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(60.dp),
                    selectedValue = "",
                    onSelectItem = { _, it ->
                        onSelectedDevice(it)
                        selectedEditDevice.value = it
                    }
                )

                Input(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    field = null,
                    placeholder = stringResource(id = R.string.name),
                    binding = name,
                    disabled = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ){
                    Button(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(62.dp)
                            .weight(1f)
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            onClickCancel()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                        border = BorderStroke(1.dp, Primary),
                    ) {
                        Text(stringResource(id = R.string.cancel), style = MaterialTheme.typography.button, fontSize = 18.sp, color = Primary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(62.dp)
                            .weight(1f)
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            if (selectedEditDevice.value.isNotEmpty() && name.value.isNotEmpty()) {
                                onClickContinue()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (selectedEditDevice.value.isNotEmpty() && name.value.isNotEmpty()) Primary2 else GrayLight,
                        ),
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                color = White,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(18.dp)
                            )
                        } else {
                            Text(stringResource(id = R.string.save), style = MaterialTheme.typography.button, fontSize = 18.sp, color = White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogDeletePlant(
    onDismiss: () -> Unit,
    onClickContinue: () -> Unit,
    onClickCancel: () -> Unit,
    deviceList: MutableList<Pair<String,String>>,
    onSelectedDevice: (String) -> Unit,
    isLoading: MutableState<Boolean>,
){
    val selectedDeleteDevice = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = Modifier
                .background(
                    BrokenWhite,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(18.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_delete_red),
                    contentDescription = "Center Image",
                    modifier = Modifier
                        .height(36.dp)
                        .width(36.dp),
                    contentScale = ContentScale.Fit,
                )

                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(id = R.string.delete_plant).uppercase(),
                    color = GrayLight,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                )

                DropDown(
                    field = null,
                    deviceList,
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(60.dp),
                    selectedValue = "",
                    onSelectItem = { _, it ->
                        onSelectedDevice(it)
                        selectedDeleteDevice.value = it
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ){
                    Button(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(62.dp)
                            .weight(1f)
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            onClickCancel()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                        border = BorderStroke(1.dp, DefaultRed),
                    ) {
                        Text(stringResource(id = R.string.cancel), style = MaterialTheme.typography.button, fontSize = 18.sp, color = DefaultRed)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(62.dp)
                            .weight(1f)
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            if (selectedDeleteDevice.value.isNotEmpty()) {
                                onClickContinue()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (selectedDeleteDevice.value.isNotEmpty()) DefaultRed else GrayLight,
                        ),
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                color = White,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(18.dp)
                            )
                        } else {
                            Text(stringResource(id = R.string.delete), style = MaterialTheme.typography.button, fontSize = 18.sp, color = White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogShowErrorDevice(
    deviceCode: String,
    language: String,
    onDismiss: () -> Unit,
) {
    val viewModel = hiltViewModel<DeviceViewModel>()
    val scope = rememberCoroutineScope()

    scope.launch {
        viewModel.setMarkerLoading.value = true
        val selectedLang = when (language) {
            "it" -> {
                "ITA"
            }
            "sr" -> {
                "SER"
            }
            else -> {
                "EN"
            }
        }
        viewModel.getAlarmDevice(deviceCode, selectedLang)
        viewModel.setMarkerLoading.value = false
    }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    BrokenWhite,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(18.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (viewModel.setMarkerLoading.value) {
                CircularProgressIndicator(
                    color = Primary,
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .align(Alignment.Center)
                )
            } else {
                LazyColumn() {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                stringResource(id = R.string.list_alarms),
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    items(viewModel.alarmDevice.value) {
                        if (!it?.code.isNullOrEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ){
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                ){
                                    Column(
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(
                                            stringResource(id = R.string.code),
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = Black,
                                            textAlign = TextAlign.Center,
                                        )
                                        Text(
                                            it?.code ?: "-",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = Black,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(
                                            stringResource(id = R.string.program),
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = Black,
                                            textAlign = TextAlign.Center,
                                        )
                                        Text(
                                            it?.program ?: "-",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = Black,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(
                                            stringResource(id = R.string.station),
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = Black,
                                            textAlign = TextAlign.Center,
                                        )
                                        Text(
                                            it?.station ?: "-",
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = Black,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        stringResource(id = R.string.description),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Black,
                                        textAlign = TextAlign.Center,
                                    )
                                    Text(
                                        it?.description ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = Black,
                                        textAlign = TextAlign.Center,
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Divider(
                                    color = GrayVeryLight,
                                    thickness = 1.dp,
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    item {
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
                                      viewModel.postResetListAlarm(deviceCode)
                                      viewModel.postDataLoading.value = false
                                  }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                            border = BorderStroke(1.dp, Primary),
                        ) {
                            if(viewModel.postDataLoading.value) {
                                CircularProgressIndicator(
                                    color = Primary,
                                    strokeCap = StrokeCap.Round,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier
                                        .width(14.dp)
                                        .height(14.dp)
                                )
                            } else {
                                Text(stringResource(id = R.string.reset), style = MaterialTheme.typography.button, fontSize = 18.sp, color = Primary)
                            }
                        }
                    }
                }
            }
        }
    }
}