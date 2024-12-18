package com.idrolife.app.presentation.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.data.api.register.RegisterRequest
import com.idrolife.app.presentation.component.CustomCheckbox
import com.idrolife.app.presentation.component.CustomTopBar
import com.idrolife.app.presentation.component.DialogSuccessRegister
import com.idrolife.app.presentation.component.Input
import com.idrolife.app.presentation.component.PasswordInput
import com.idrolife.app.presentation.viewmodel.AuthViewModel
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Constants.PRIVACY_POLICY_URL
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<AuthViewModel>()
    val scope = rememberCoroutineScope()

    val window = (context as Activity).window
    val view = LocalView.current
    Helper().setNotifBarColor(view, window, White.toArgb(),true)

    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val cpassword = remember { mutableStateOf("") }
    val checked = remember { mutableStateOf(false) }
    val showDialogSuccessRegister = remember { mutableStateOf(false) }

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

            if (email.value.isNotEmpty() &&
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.email_must_valid),
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PasswordInput(
                modifier = Modifier,
                placeholder = stringResource(id = R.string.password),
                binding = password,
                imeAction = ImeAction.Next,
            )

            if (password.value.isNotEmpty() &&
                password.value.length < 8) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.password_must_8_char),
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PasswordInput(
                modifier = Modifier,
                placeholder = stringResource(id = R.string.confirm_password),
                binding = cpassword,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = {

                    }
                )
            )

            if (cpassword.value.isNotEmpty() &&
                cpassword.value != password.value) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.password_must_match),
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary,
                    ),
                )
            }

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
                    onClick = {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
                        context.startActivity(browserIntent)
                    },
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.privacy_policy),
                        style = TextStyle(
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary,
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
                onClick = {
                    if (
                        name.value.isNotEmpty() &&
                        password.value.isNotEmpty() &&
                        surname.value.isNotEmpty() &&
                        email.value.isNotEmpty() &&
                        checked.value &&
                        password.value.length >= 8 &&
                        password.value == cpassword.value &&
                        android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
                    ) {
                        scope.launch {
                            viewModel.loading.value = true
                            val successRegist = viewModel.register(RegisterRequest(
                                first_name = name.value,
                                last_name = surname.value,
                                email = email.value,
                                password = password.value,
                                password_confirmation = cpassword.value,
                            ))

                            if (successRegist == null) {
                                showDialogSuccessRegister.value = true
                            } else {
                                Toast.makeText(context, successRegist, Toast.LENGTH_LONG).show()
                            }
                            viewModel.loading.value = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (
                        name.value.isNotEmpty() &&
                        password.value.isNotEmpty() &&
                        surname.value.isNotEmpty() &&
                        email.value.isNotEmpty() &&
                        checked.value &&
                        password.value.length >= 8 &&
                        password.value == cpassword.value &&
                        android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
                    ) Primary else GrayLight
                ),
            ) {
                if (viewModel.loading.value) {
                    CircularProgressIndicator(
                        color = White,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp)
                    )
                } else {
                    Text(stringResource(id = R.string.submit), style = MaterialTheme.typography.button, fontSize = 18.sp)
                }
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
                            color = Primary,
                        ),
                    )
                }
            }
        }

        if (showDialogSuccessRegister.value) {
            DialogSuccessRegister(
                onDismiss = {
                    showDialogSuccessRegister.value = false
                },
                onClickContinue = {
                    navController.navigateUp()
                }
            )
        }
    }
}
