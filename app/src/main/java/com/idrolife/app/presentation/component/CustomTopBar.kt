package com.idrolife.app.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary

@Composable
fun CustomTopBar(
    title: String,
    navController: NavController? = null,
    customBackPress: (() -> Unit)? = null,
    darkIcon: Boolean? = true
) {
    val withBackButton = navController?.previousBackStackEntry != null

    Column(
        modifier = Modifier.padding(top=24.dp)
    ) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (withBackButton) {
                        IconButton(
                            onClick = {
                                if (customBackPress != null) {
                                    customBackPress()
                                } else {
                                    navController?.navigateUp()
                                }
                            },
                            modifier = Modifier
                                .width(36.dp)
                                .height(36.dp)
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(24.dp),
                                bitmap = ImageBitmap.imageResource(if (darkIcon == true) R.drawable.ic_arrow_back_green else R.drawable.ic_arrow_back_white),
                                contentDescription = "back button"
                            )
                        }
                    }

                    Text(
                        title,
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary,
                            fontFamily = Manrope,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = if (withBackButton) 36.dp else 0.dp),
                        textAlign = TextAlign.Left
                    )
                }
            },
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        )
    }
}

@Composable
fun CustomTopBarSimple(navController: NavController, title: String){
    TopAppBar(
        modifier = Modifier,
        title = {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .size(28.dp),
                        bitmap = ImageBitmap.imageResource(R.drawable.ic_arrow_back_black),
                        contentDescription = "back button"
                    )
                }

                Text(
                    title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Black,
                        fontFamily = Manrope,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 36.dp),
                    textAlign = TextAlign.Center
                )
            }
        },
        backgroundColor = BrokenWhite,
        elevation = 2.dp
    )
}
