package com.idrolife.app.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RangeSlider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.idrolife.app.R
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BlackSoft
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.GrayVeryLight
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Green
import com.idrolife.app.theme.Green2
import com.idrolife.app.theme.GreenLight
import com.idrolife.app.theme.GreenVeryLight
import com.idrolife.app.theme.InputPlaceholderGray
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.PlaceholderGray
import com.idrolife.app.theme.White

@Composable
fun TrailingIcon(@DrawableRes id: Int, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Image(
            bitmap = ImageBitmap.imageResource(id),
            contentDescription = "Show password icon",
            modifier = Modifier
                .size(24.dp)
        )
    }
}

@Composable
fun Input(
    placeholder: String,
    modifier: Modifier = Modifier,
    field: String? = null,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions? = null,
    binding: MutableState<String>,
    disabled: Boolean = false,
    @DrawableRes trailingIcon: Int? = null,
    onTrailingIconClick: (() -> Unit) = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colorConfig: TextFieldColors? = null,
    modifierParent: Modifier = Modifier,
    trailingUnit: String? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    val trailingIc: @Composable (() -> Unit) = {
        if (trailingIcon != null) {
            TrailingIcon(trailingIcon, onTrailingIconClick)
        }
    }

    Column(
        modifier = modifierParent,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (field != null) {
            Text(field,
                fontFamily = Manrope,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Black,
            )
        }

        Row {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isFocused) GreenVeryLight else White)
                    .weight(1f)
            ) {
                OutlinedTextField(
                    value = binding.value,
                    readOnly = disabled,
                    onValueChange = { binding.value = it },
                    placeholder = {
                        Text(
                            placeholder,
                            color = InputPlaceholderGray,
                            style = MaterialTheme.typography.body2
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            isFocused = it.isFocused
                        }
                        .then(modifier),
                    singleLine = true,
                    keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
                    keyboardActions = keyboardActions ?: KeyboardActions.Default,
                    visualTransformation = visualTransformation,
                    trailingIcon = trailingIc,
                    colors = colorConfig ?: TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Green,
                        focusedBorderColor = Green,
                        unfocusedBorderColor = PlaceholderGray,
                        cursorColor = Green,
                        disabledBorderColor = Green,
                    ),
                    shape = RoundedCornerShape(14.dp),
                )
            }

            if (trailingUnit != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(trailingUnit,
                    modifier = Modifier
                        .align(Alignment.Bottom),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                )
            }
        }
    }
}

@Composable
fun PasswordInput(
    modifier: Modifier = Modifier,
    field: String? = null,
    placeholder: String,
    binding: MutableState<String>,
    disabled: Boolean = false,
    imeAction: ImeAction? = null,
    keyboardActions: KeyboardActions? = null,
) {
    val showPassword = remember { mutableStateOf(false) }
    val passwordIcon = if (showPassword.value) R.drawable.eye else R.drawable.eye_invisible
    val togglePasswordVisibility = { showPassword.value = !showPassword.value }
    val visualTransformation =
        if (!showPassword.value) PasswordVisualTransformation()
        else VisualTransformation.None

    Input(
        field = field,
        placeholder = placeholder,
        binding = binding,
        disabled = disabled,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction ?: ImeAction.Default
        ),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = passwordIcon,
        onTrailingIconClick = togglePasswordVisibility,
        modifier = modifier,
    )
}



@Composable
fun InputWithInitial(
    placeholder: String,
    modifier: Modifier = Modifier,
    field: String? = null,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions? = null,
    initialValue: String,
    disabled: Boolean = false,
    @DrawableRes trailingIcon: Int? = null,
    onTrailingIconClick: (() -> Unit) = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colorConfig: TextFieldColors? = null,
    modifierParent: Modifier = Modifier,
    trailingUnit: String? = null,
    onTextChanged: (String) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(initialValue) }

    val trailingIc: @Composable (() -> Unit) = {
        if (trailingIcon != null) {
            TrailingIcon(trailingIcon, onTrailingIconClick)
        }
    }

    Column(
        modifier = modifierParent,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (field != null) {
            Text(field,
                fontFamily = Manrope,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Black,
            )
        }

        Row {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isFocused) GreenVeryLight else White)
                    .weight(1f)
            ) {
                OutlinedTextField(
                    value = value,
                    readOnly = disabled,
                    onValueChange = {
                        value = it
                        onTextChanged(it)
                    },
                    placeholder = {
                        Text(
                            placeholder,
                            color = InputPlaceholderGray,
                            style = MaterialTheme.typography.body2
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            isFocused = it.isFocused
                        }
                        .then(modifier),
                    singleLine = true,
                    keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
                    keyboardActions = keyboardActions ?: KeyboardActions.Default,
                    visualTransformation = visualTransformation,
                    trailingIcon = trailingIc,
                    colors = colorConfig ?: TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Green,
                        focusedBorderColor = Green,
                        unfocusedBorderColor = PlaceholderGray,
                        cursorColor = Green,
                        disabledBorderColor = Green,
                    ),
                    shape = RoundedCornerShape(14.dp),
                )
            }

            if (trailingUnit != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(trailingUnit,
                    modifier = Modifier
                        .align(Alignment.Bottom),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                )
            }
        }
    }
}

@Composable
fun PasswordInputWithInitial(
    modifier: Modifier = Modifier,
    field: String? = null,
    placeholder: String,
    initialValue: String,
    disabled: Boolean = false,
    imeAction: ImeAction? = null,
    keyboardActions: KeyboardActions? = null,
    onTextChanged: (String) -> Unit,
) {
    val showPassword = remember { mutableStateOf(false) }
    val passwordIcon = if (showPassword.value) R.drawable.eye else R.drawable.eye_invisible
    val togglePasswordVisibility = { showPassword.value = !showPassword.value }
    val visualTransformation =
        if (!showPassword.value) PasswordVisualTransformation()
        else VisualTransformation.None

    InputWithInitial(
        field = field,
        placeholder = placeholder,
        initialValue = initialValue,
        disabled = disabled,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction ?: ImeAction.Default
        ),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = passwordIcon,
        onTrailingIconClick = togglePasswordVisibility,
        modifier = modifier,
        onTextChanged = onTextChanged
    )
}

@Composable
fun DropDown(
    field: String?,
    items: MutableList<Pair<String,String>>,
    modifier: Modifier,
    selectedValue: String,
    onSelectItem: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var boxSize by remember { mutableStateOf(Size.Zero) }
    var value by remember { mutableStateOf(
        if (items.filter { it.second == selectedValue }.isNotEmpty())
            items.filter { it.second == selectedValue }[0].first
        else "",) }

    Column(
        modifier = Modifier.then(modifier),
    ) {
        if (field != null) {
            Text(field,
                fontFamily = Manrope,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Black,
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
                .clip(RoundedCornerShape(14.dp))
                .border(
                    border = BorderStroke(1.dp, GrayLight),
                    shape = RoundedCornerShape(14.dp)
                )
                .onGloballyPositioned { layoutCoordinates ->
                    boxSize = layoutCoordinates.size.toSize()
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(
                    value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { expanded = true })
                        .background(White)
                        .padding(top = 19.dp, bottom = 19.dp, start = 18.dp)
                        .weight(1f),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Green,
                )

                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Image(
                        bitmap = ImageBitmap.imageResource(R.drawable.ic_arrow_down_black),
                        contentDescription = "Dropdown",
                        modifier = Modifier
                            .size(12.dp)
                    )
                }
            }

            DropdownMenu(
                modifier = Modifier
                    .width(with(LocalDensity.current) { boxSize.width.toDp() })
                    .background(White),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                items.forEachIndexed { index, s ->
                    DropdownMenuItem(
                        onClick = {
                        expanded = false
                        value = s.first
                        onSelectItem(s.second)
                    }) {
                        Text(s.first,
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
}

@Composable
fun CheckBoxWithTitle(
    field:String? = null,
    checkedTitle: String,
    uncheckedTitle: String,
    modifier: Modifier?,
    selectedValue: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    val checked = remember { mutableStateOf(selectedValue) }
    val switchPadding by animateDpAsState(targetValue = if (checked.value) 70.dp else 0.dp)
    val switchRightPaddingText by animateDpAsState(targetValue = if (checked.value) 8.dp else 0.dp)
    val switchLeftPaddingText by animateDpAsState(targetValue = if (checked.value) 0.dp else 12.dp)

    Column(
        modifier = modifier ?: Modifier,
    ) {
        if (field != null) {
            Text(
                field,
                fontFamily = Manrope,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Black,
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .width(110.dp)
                .height(40.dp)
                .background(
                    color = if (checked.value) Green else BlackSoft,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(4.dp)
                .clickable {
                    checked.value = !checked.value
                    onChecked(checked.value)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = if (checked.value) checkedTitle else uncheckedTitle,
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RangedSeekbar(
    field:String? = null,
    min: Int,
    max: Int,
    modifier: Modifier?,
    currentMin: Int?,
    currentMax: Int?,
    onValueChanged: (Int, Int) -> Unit,
) {
    val value = remember { mutableStateOf((currentMin?.toFloat() ?: 0f) .. (currentMax?.toFloat() ?: 0f)) }
    val edValueMin = remember { mutableStateOf((currentMin ?: 0)) }
    val edValueMax = remember { mutableStateOf((currentMax ?: 0)) }

    Column(
        modifier = modifier ?: Modifier,
    ) {
        if (field != null) {
            Text(
                field,
                fontFamily = Manrope,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Black,
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Min",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Black,
                )

                Spacer(modifier= Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(70.dp)
                        .background(
                            GrayVeryVeryLight,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicTextField(
                            value = edValueMin.value.toInt().toString(),
                            onValueChange = {
                                var str = it.replace(" ", "")
                                if (str.isEmpty()) {
                                    str = "0"
                                }

                                if (str.toFloat() > max) {
                                    str = max.toString()
                                } else if (str.toFloat() < min) {
                                    str = min.toString()
                                }

                                edValueMin.value = str.toInt()
                                onValueChanged(edValueMin.value, edValueMax.value)
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Left,
                                color = Black,
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number,
                            ),
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Max",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Black,
                )

                Spacer(modifier= Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(70.dp)
                        .background(
                            GrayVeryVeryLight,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicTextField(
                            value = edValueMax.value.toInt().toString(),
                            onValueChange = {
                                var str = it.replace(" ", "")
                                if (str.isEmpty()) {
                                    str = "0"
                                }

                                if (str.toInt() > max) {
                                    str = max.toString()
                                } else if (str.toInt() < min) {
                                    str = min.toString()
                                }
                                edValueMax.value = str.toInt()
                                onValueChanged(edValueMin.value, edValueMax.value)
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Left,
                                color = Black,
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number,
                            ),
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp) // Set the height for the track thickness
        ) {
            RangeSlider(
                value = value.value,
                steps = (max - 1),
                onValueChange = { range ->
                    value.value = range
                    edValueMin.value = range.start.toInt()
                    edValueMax.value = range.endInclusive.toInt()
                },
                valueRange = min.toFloat()..max.toFloat(),
                onValueChangeFinished = {
                    if (edValueMin.value > edValueMax.value) {
                        edValueMin.value = edValueMax.value
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = Green2,               // Color of the thumb
                    activeTickColor = GreenLight,   // Color of the active ticks
                    inactiveTickColor = GrayVeryLight,    // Color of the inactive ticks
                )
            )
        }
    }
}