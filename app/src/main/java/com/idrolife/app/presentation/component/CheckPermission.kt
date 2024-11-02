package com.idrolife.app.presentation.component

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermission(
    multiplePermissionsState: MultiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    ),
    onPermissionGranted: () -> Unit,
    onPermissionDenied: (text: String) -> Unit
) {
    if (multiplePermissionsState.allPermissionsGranted) {
        // If all permissions are granted, then show screen with the feature enabled
        onPermissionGranted()
    } else {
        onPermissionDenied("App need location to function!")
    }
}