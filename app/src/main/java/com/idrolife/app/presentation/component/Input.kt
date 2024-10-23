package com.idrolife.app.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.idrolife.app.R
import com.idrolife.app.theme.Green
import com.idrolife.app.theme.GreenVeryLight
import com.idrolife.app.theme.InputPlaceholderGray
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
) {
    var isFocused by remember { mutableStateOf(false) }

    val trailingIc: @Composable (() -> Unit) = {
        if (trailingIcon != null) {
            TrailingIcon(trailingIcon, onTrailingIconClick)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (field != null) {
            Text(field, style = MaterialTheme.typography.body2)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isFocused) GreenVeryLight else White)
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
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Green,
                    focusedBorderColor = Green,
                    unfocusedBorderColor = PlaceholderGray,
                    cursorColor = Green,
                    disabledBorderColor = Green,
                ),
            )
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
        modifier = modifier
    )
}