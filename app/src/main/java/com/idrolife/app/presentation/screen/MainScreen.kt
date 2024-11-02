package com.idrolife.app.presentation.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.idrolife.app.R
import com.idrolife.app.data.api.device.DevicesItem
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.DeviceStatusCard
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Gray
import com.idrolife.app.theme.GrayVeryLight
import com.idrolife.app.theme.Green
import com.idrolife.app.theme.GreenLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val tabs = listOf(getString(context, R.string.my_plants), getString(context, R.string.account))
    val icons = listOf(
        painterResource(id = R.drawable.ic_tab1),
        painterResource(id = R.drawable.ic_tab2),
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        TopIndicator(GreenLight, Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]))
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Helper().setNotifBarColor(view, window, Green.toArgb(),false)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Green)
            .padding(vertical = 12.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.img_idrolife_white),
                contentDescription = "Center Image",
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(52.dp)
                    .width(260.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Scaffold(
            backgroundColor = Color.Transparent,
            bottomBar = {
                // TabRow at the bottom
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = indicator,
                    backgroundColor = Green,
                    contentColor = White,
                ) {
                    tabs.forEachIndexed { index, title ->
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .height(50.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                painter = icons[index],
                                contentDescription = title,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(title, style = MaterialTheme.typography.button
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            HorizontalPager(
                count = tabs.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = Color.Transparent)
            ) { page ->
                when (page) {
                    0 -> Tab1(navController)
                    1 -> Text("2")
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Tab1(navController: NavController){
    val context = LocalContext.current
    val searchValue = remember { mutableStateOf("") }

    val viewModel = hiltViewModel<DeviceViewModel>()

    val devices = mutableStateOf<List<DevicesItem?>>(emptyList())

    LaunchedEffect(Unit) {
        viewModel.isLoading.value = true

        val result = viewModel.getDevices()

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

        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevices()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokenWhite)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = stringResource(id = R.string.my_plants),
            style = TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
            )
        )

        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .background(
                    GrayVeryLight,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_search_grey),
                    contentDescription = "Center Image",
                    modifier = Modifier
                        .height(32.dp)
                        .width(32.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit,
                )
                BasicTextField(
                    value = searchValue.value,
                    onValueChange = {searchValue.value = it},
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Left,
                    ),
                    decorationBox = { innerTextField ->
                        if (searchValue.value.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.search_plant),
                                color = Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = Manrope,
                                textAlign = TextAlign.Left,
                            )
                        }
                        innerTextField()
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                    )
                )
            }
        }

        LazyColumn {
            item {
                if (viewModel.isLoading.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Green,
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                } else {
                    for (device in viewModel.devices.value) {
                        DeviceStatusCard(
                            data = device,
                            onClick = {
                                navController.navigate(Screen.DetailDevice.withArgs(device?.id.toString(), device?.name ?: "", device?.code ?: ""))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopIndicator(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .offset(y = (-70).dp)
            .height(4.dp)
            .background(color = color)
    )
}