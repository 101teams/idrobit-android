package com.idrolife.app.presentation.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.presentation.component.CustomTopBar
import com.idrolife.app.presentation.component.Input
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Green
import com.idrolife.app.utils.Helper

@Composable
fun ForgotPasswordScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current
    Helper().setNotifBarColor(view, window, BrokenWhite.toArgb(),true)

    val email = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokenWhite),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTopBar(title = stringResource(id = R.string.password_recovery), navController)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.forgot_password_enter_email),
                style = MaterialTheme.typography.body1,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Input(
                modifier = Modifier,
                field = null,
                placeholder = stringResource(id = R.string.email),
                binding = email,
                disabled = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
            )

            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .padding(top = 48.dp)
                    .height(50.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(0.dp),
                onClick = {  },
                colors = ButtonDefaults.buttonColors(backgroundColor = Green),
            ) {
                Text(stringResource(id = R.string.submit), style = MaterialTheme.typography.button, fontSize = 18.sp)
            }
        }
    }
}
