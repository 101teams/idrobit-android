package com.idrolife.app.presentation.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.idrolife.app.R
import com.idrolife.app.data.api.device.EditPlantRequest
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.DeviceStatusCard
import com.idrolife.app.presentation.component.DialogDeletePlant
import com.idrolife.app.presentation.component.DialogEditPlant
import com.idrolife.app.presentation.component.DialogShowErrorDevice
import com.idrolife.app.presentation.component.DropDown
import com.idrolife.app.presentation.viewmodel.AuthViewModel
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.DefaultRed
import com.idrolife.app.theme.Gray
import com.idrolife.app.theme.GrayVeryLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.PrimaryLight
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import com.idrolife.app.utils.PrefManager
import kotlinx.coroutines.launch
import java.util.Locale

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

    val viewModel = hiltViewModel<DeviceViewModel>()

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        TopIndicator(PrimaryLight, Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]))
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Helper().setNotifBarColor(view, window, Primary.toArgb(),false)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        val result = viewModel.getRelatedDevice()
        viewModel.searchDeviceValue.value = ""

        if (result.second == "Unauthorized") {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        } else if (!result.second.isNullOrBlank() && !result.second!!.contains("coroutine scope")) {
            Toast.makeText(context, result.second, Toast.LENGTH_LONG)
                .show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Primary)
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
                    backgroundColor = Primary,
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
                    0 -> Tab1(navController, viewModel)
                    1 -> Tab2(navController)
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun Tab1(navController: NavController, viewModel: DeviceViewModel){
    val context = LocalContext.current
    val searchValue = remember { mutableStateOf("") }

    val isDeviceMenuExpanded = remember { mutableStateOf(false) }

    val editDeviceName = remember { mutableStateOf("") }
    val selectedEditDevice = remember { mutableStateOf("") }
    val selectedDeleteDevice = remember { mutableStateOf("") }
    val showEditPlant = remember { mutableStateOf(false) }
    val showDeletePlant = remember { mutableStateOf(false) }

    val showDeviceError = remember { mutableStateOf(false) }
    val selectedDeviceCode = remember { mutableStateOf("") }

    val relatedDevice = remember { mutableStateOf(mutableListOf(Pair("",""))) }
    val relatedDeviceVM by viewModel.deviceRelated.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(relatedDeviceVM){
        val data = mutableListOf<Pair<String, String>>()
        for (i in relatedDeviceVM) {
            if (i?.id != null && i.name != null) {
                data.add(Pair(i.name, i.id.toString()))
            }
        }
        relatedDevice.value = data
    }

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

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(text = stringResource(id = R.string.my_plants),
                style = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                )
            )

            Column {
                IconButton(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Primary2)
                        .size(36.dp)
                        .padding(8.dp),
                    onClick = {
                        isDeviceMenuExpanded.value = true
                    }
                ) {
                    Image(
                        bitmap = ImageBitmap.imageResource(R.drawable.ic_menu_white),
                        contentDescription = "Dropdown",
                        modifier = Modifier
                            .fillMaxSize(),
                    )
                }

                DropdownMenu(
                    expanded = isDeviceMenuExpanded.value,
                    onDismissRequest = { isDeviceMenuExpanded.value = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            isDeviceMenuExpanded.value = false
                            navController.navigate(Screen.CreatePlant.route)
                        }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                bitmap = ImageBitmap.imageResource(R.drawable.img_add_device),
                                contentDescription = stringResource(id = R.string.create),
                                modifier = Modifier
                                    .size(30.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(id = R.string.create),
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Black,
                            )
                        }
                    }
                    
                    DropdownMenuItem(
                        onClick = {
                            isDeviceMenuExpanded.value = false
                            showEditPlant.value = true
                        }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                bitmap = ImageBitmap.imageResource(R.drawable.img_edit_device),
                                contentDescription = stringResource(id = R.string.edit),
                                modifier = Modifier
                                    .size(30.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(id = R.string.edit),
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Black,
                            )
                        }
                    }
                    
                    DropdownMenuItem(
                        onClick = {
                            isDeviceMenuExpanded.value = false
                            showDeletePlant.value = true
                        }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                bitmap = ImageBitmap.imageResource(R.drawable.img_delete_device),
                                contentDescription = "Delete Device",
                                modifier = Modifier
                                    .size(30.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(id = R.string.delete),
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Black,
                            )
                        }
                    }
                }
            }
        }

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
                    onValueChange = {
                        searchValue.value = it
                        viewModel.searchDeviceValue.value = it
                        viewModel._filteredDevices.value = viewModel.devices.value.filter {value ->
                            value?.name != null && value.name.lowercase().contains(it.lowercase())
                        }
                    },
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
                    for (device in viewModel.filteredDevices.value) {
                        DeviceStatusCard(
                            data = device,
                            onClick = {
                                navController.navigate(Screen.DetailDevice.withArgs(device?.id.toString(), device?.name ?: "", device?.code ?: ""))
                            },
                            showDeviceError = showDeviceError,
                            selectedDeviceCode =  selectedDeviceCode,
                        )
                    }
                }
            }
        }

        if (showEditPlant.value){
            DialogEditPlant(
                onDismiss = {
                    showEditPlant.value = false
                    editDeviceName.value = ""
                    selectedEditDevice.value = ""
                },
                onClickContinue = {
                    scope.launch {
                        viewModel.postDataLoading.value = true
                        val response = viewModel.postEditPlant(
                            EditPlantRequest(
                                name = editDeviceName.value,
                            ),
                            selectedEditDevice.value.toInt()
                        )

                        viewModel.getRelatedDevice()
                        viewModel.getDevices()

                        viewModel.postDataLoading.value = false

                        if (response.first) {
                            showEditPlant.value = false
                            editDeviceName.value = ""
                            selectedEditDevice.value = ""
                        } else if (!response.second.isNullOrBlank() && !response.second!!.contains("coroutine scope")) {
                            Toast.makeText(context, response.second, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                },
                onClickCancel = {
                    showEditPlant.value = false
                },
                name = editDeviceName,
                deviceList = relatedDevice.value,
                onSelectedDevice = {
                    selectedEditDevice.value = it
                },
                isLoading = viewModel.postDataLoading,
            )
        }

        if (showDeletePlant.value){
            DialogDeletePlant(
                onDismiss = {
                    showDeletePlant.value = false
                    selectedDeleteDevice.value = ""
                },
                onClickContinue = {
                    scope.launch {
                        viewModel.postDataLoading.value = true
                        val response = viewModel.postDeletePlant(
                            selectedDeleteDevice.value.toInt()
                        )

                        viewModel.getRelatedDevice()
                        viewModel.getDevices()

                        viewModel.postDataLoading.value = false

                        if (response.first) {
                            showDeletePlant.value = false
                            selectedDeleteDevice.value = ""
                        } else if (!response.second.isNullOrBlank() && !response.second!!.contains("coroutine scope")) {
                            Toast.makeText(context, response.second, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                },
                onClickCancel = {
                    showDeletePlant.value = false
                },
                deviceList = relatedDevice.value,
                onSelectedDevice = {
                    selectedDeleteDevice.value = it
                },
                isLoading = viewModel.postDataLoading,
            )
        }

        if (showDeviceError.value) {
            DialogShowErrorDevice(
                deviceCode = selectedDeviceCode.value,
                language = PrefManager(context).getCurrentLanguage(),
                onDismiss = {
                    showDeviceError.value = false
                }
            )
        }
    }
}

@Composable
fun Tab2(navController: NavController){
    val context = LocalContext.current

    val prefManager = PrefManager(context)

    val authViewModel = hiltViewModel<AuthViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokenWhite)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Box(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .background(
                    Color(0xFFDFF4DD),
                    shape = RoundedCornerShape(14.dp)
                )
                .size(280.dp),
        ){
            Image(
                painter = painterResource(id = R.drawable.img_account_white),
                contentDescription = "Center Image",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "${if (prefManager.getUser()?.firstName != null) prefManager.getUser()?.firstName else {"-"}} ${if (prefManager.getUser()?.lastName != null) prefManager.getUser()?.lastName else {""}}", fontSize = 20.sp, color = Black, fontFamily = Manrope, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        DropDown(
            field = stringResource(id = R.string.language),
            mutableListOf(
                Pair(
                    "ITA",
                    "it"
                ),
                Pair(
                    "EN",
                    "en"
                ),
                Pair(
                    "SR",
                    "sr"
                ),
            ),
            Modifier
                .height(84.dp),
            selectedValue = context.resources.configuration.locales[0].language,
            onSelectItem = { _, it ->
                prefManager.setCurrentLanguage(it)
                val appLocale = LocaleListCompat.forLanguageTags(it)
                AppCompatDelegate.setApplicationLocales(appLocale)

                val config = Configuration(context.resources.configuration)
                val locale = Locale(it)
                Locale.setDefault(locale)
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
            }
        )

        Button(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .height(62.dp)
                .fillMaxWidth()
                .padding(top = 12.dp),
            contentPadding = PaddingValues(0.dp),
            onClick = {
                authViewModel.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = White,),
            border = BorderStroke(1.dp, DefaultRed),
        ) {
            Text(stringResource(id = R.string.sign_out), fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DefaultRed)
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