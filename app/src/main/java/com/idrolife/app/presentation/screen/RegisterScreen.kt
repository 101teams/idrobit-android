package com.idrolife.app.presentation.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.presentation.component.CustomCheckbox
import com.idrolife.app.presentation.component.CustomTopBar
import com.idrolife.app.presentation.component.Input
import com.idrolife.app.presentation.component.PasswordInput
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Green
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper

@Composable
fun RegisterScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current
    Helper().setNotifBarColor(view, window, White.toArgb(),true)

    val name = remember { mutableStateOf<String>("") }
    val surname = remember { mutableStateOf<String>("") }
    val email = remember { mutableStateOf<String>("") }
    val password = remember { mutableStateOf<String>("") }
    val cpassword = remember { mutableStateOf<String>("") }
    val checked = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokenWhite),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTopBar(title = stringResource(id = R.string.register), navController)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Input(
                modifier = Modifier,
                field = null,
                placeholder = stringResource(id = R.string.name),
                binding = name,
                disabled = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Input(
                modifier = Modifier,
                field = null,
                placeholder = stringResource(id = R.string.surname),
                binding = surname,
                disabled = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
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

            Spacer(modifier = Modifier.height(12.dp))

            PasswordInput(
                modifier = Modifier,
                placeholder = stringResource(id = R.string.password),
                binding = password,
                imeAction = ImeAction.Next,
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordInput(
                modifier = Modifier,
                placeholder = "Confirm Password",
                binding = cpassword,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = {

                    }
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                CustomCheckbox(stringResource(id = R.string.i_have_read), checked = checked.value) {
                    checked.value = it
                }

                TextButton(
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    onClick = { },
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.privacy_policy),
                        style = TextStyle(
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Green,
                        ),
                    )
                }
            }

            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .height(62.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {  },
                colors = ButtonDefaults.buttonColors(backgroundColor = Green),
            ) {
                Text(stringResource(id = R.string.submit), style = MaterialTheme.typography.button, fontSize = 18.sp)
            }

            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.already_have_an_account),
                    style = MaterialTheme.typography.body1,
                )

                TextButton(
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    onClick = {
                        navController.navigateUp()
                    },
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.login_here),
                        style = TextStyle(
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Green,
                        ),
                    )
                }
            }
        }
    }
}
