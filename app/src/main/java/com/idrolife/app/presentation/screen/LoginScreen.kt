package com.idrolife.app.presentation.screen

import android.app.Activity
import android.content.res.Configuration
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.data.api.auth.AuthRequest
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.Input
import com.idrolife.app.presentation.component.PasswordInput
import com.idrolife.app.presentation.viewmodel.AuthViewModel
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Gray
import com.idrolife.app.theme.GrayVeryLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import com.idrolife.app.utils.PrefManager
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val email = remember { mutableStateOf<String>("") }
    val password = remember { mutableStateOf<String>("") }

    val window = (context as Activity).window
    val view = LocalView.current
    Helper().setNotifBarColor(view, window, Primary.toArgb(),false)


    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<AuthViewModel>()
    val loading = viewModel.loading.value
    val focusManager = LocalFocusManager.current

    val prefManager = PrefManager(context)

    var selectedLanguage = remember { mutableStateOf("it") }
    val languages = mutableListOf(
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
        )

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    fun login() {
        val request = AuthRequest(
            email = email.value ?: "",
            password = password.value ?: ""
        )

        if (request.email.isBlank()) {
            showToast(getString(context, R.string.email_cannot_empty))
            return
        }

        if (request.password.isBlank()) {
            showToast(getString(context, R.string.password_cannot_empty))
            return
        }

        if (request.password.length < 8) {
            showToast(getString(context, R.string.password_must_8_char))
            return
        }

        focusManager.clearFocus()

        scope.launch {
            val error = viewModel.login(request)

            if (error == null) {
                viewModel.resetToken()

                navController.navigate(Screen.Main.route) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            } else {
                showToast(error, Toast.LENGTH_LONG)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokenWhite)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = "Center Image",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(260.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillBounds,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 210.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_idrolife),
                contentDescription = "Center Image",
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.FillBounds,
            )
            
            Text(
                text = "IdroLife",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary,
                    fontSize = 32.sp
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            Input(
                modifier = Modifier,
                field = null,
                placeholder = stringResource(id = R.string.your_email),
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
                placeholder = stringResource(id = R.string.your_password),
                binding = password,
                imeAction = ImeAction.Done,
            )
            
            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
            ) {
                Text(
                    text = stringResource(id = R.string.forgot_password),
                    style = MaterialTheme.typography.body1,
                )
            }

            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .height(62.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                      login()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary),
            ) {
                Text(stringResource(id = R.string.login), style = MaterialTheme.typography.button, fontSize = 18.sp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically // Memastikan teks dan garis berada di tengah vertikal
            ) {
                Divider(
                    color = Gray,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f) // Membuat divider kiri memenuhi ruang
                )

                Spacer(modifier = Modifier.width(8.dp)) // Memberi jarak antara teks dan divider

                Text(
                    text = stringResource(id = R.string.or),
                    fontSize = 16.sp // Ganti ukuran font sesuai kebutuhan
                )

                Spacer(modifier = Modifier.width(8.dp)) // Memberi jarak antara teks dan divider

                Divider(
                    color = Gray,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f) // Membuat divider kanan memenuhi ruang
                )
            }

            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .height(62.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    navController.navigate(Screen.Register.route)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                border = BorderStroke(1.dp, Primary),
            ) {
                Text(stringResource(id = R.string.register), style = MaterialTheme.typography.button, fontSize = 18.sp, color = Primary)
            }
        }

        Row(
            modifier = Modifier
                .padding(end = 24.dp, bottom = 24.dp)
                .background(GrayVeryLight, RoundedCornerShape(8.dp))
                .align(Alignment.BottomEnd)
                .padding(4.dp)
        ) {
            languages.forEach { language ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(
                            if (selectedLanguage.value == language.second) White else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            selectedLanguage.value = language.second

                            prefManager.setCurrentLanguage(language.second)
                            val appLocale = LocaleListCompat.forLanguageTags(language.second)
                            AppCompatDelegate.setApplicationLocales(appLocale)

                            val config = Configuration(context.resources.configuration)
                            val locale = Locale(language.second)
                            Locale.setDefault(locale)
                            context.resources.updateConfiguration(config, context.resources.displayMetrics)
                        }
                        .padding(8.dp)
                ) {
                    Text(
                        text = language.first,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Manrope,
                        color = if (selectedLanguage.value == language.second) Primary else Gray,
                    )
                }
            }
        }
    }
}
